package com.accountshop.controller;

import com.accountshop.dto.CartItem;
import com.accountshop.dto.request.CheckoutProduct;
import com.accountshop.dto.request.PaymentRequest;
import com.accountshop.entity.*;
import com.accountshop.repository.*;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.CartService;
import com.accountshop.service.ChatService;
import com.accountshop.service.OrderService;
import com.accountshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;
    private final OrderRepository orderRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductQuestionRepository productQuestionRepository;
    private final ChatService chatService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final VariantPricingRepository variantPricingRepository;
    public static final String API_KEY = "b1badf41-73c3-4f04-8b4d-70c5ee0828da";
    public static final String CLIENT_ID = "ff8d8ec5-ccc0-4d72-9c1b-ff0fa5e2d5bb";
    public static final String CHECK_SUM_KEY = "3e6d69ac4db2ced7a7ec9de5bd28b48afc958e7dbe98d537730a6a7e63ed6a7d";
    private void addCurrentUser(Model model) {
        securityUtils.getCurrentUser().ifPresent(user -> model.addAttribute("currentUser", user));
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAsc());
        model.addAttribute("featuredProducts", productRepository.findAllActive(PageRequest.of(0, 8)));
        addCurrentUser(model);
        return "user/home";
    }

    @GetMapping("/products")
    public String explore(@RequestParam(required = false) Long categoryId,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "12") int size,
                         Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAsc());
        if (categoryId != null) {
            model.addAttribute("products", productRepository.findAllActiveByCategory(categoryId, PageRequest.of(page, size)));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("products", productRepository.findAllActive(PageRequest.of(page, size)));
        }
        addCurrentUser(model);
        return "user/explore";
    }

    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        var product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        model.addAttribute("product", product);
        model.addAttribute("reviews", productReviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId(), PageRequest.of(0, 10)));
        model.addAttribute("questions", productQuestionRepository.findByProductId(product.getId(), PageRequest.of(0, 20)).getContent());
        addCurrentUser(model);
        return "user/product-detail";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        return "user/dashboard";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        var pageable = PageRequest.of(page, 10);
        if (status != null && !status.isEmpty()) {
            model.addAttribute("orders", orderRepository.findByBuyerIdAndStatusOrderByCreatedAtDesc(user.getId(), status, pageable));
            model.addAttribute("selectedStatus", status);
        } else {
            model.addAttribute("orders", orderRepository.findByBuyerIdOrderByCreatedAtDesc(user.getId(), pageable));
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1); // Will be calculated from Page object
        return "user/orders";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        addCurrentUser(model);
        return "user/cart";
    }

    /* ════════════════════════════════════
       CHECKOUT
       ════════════════════════════════════ */
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(required = false) List<Long> pricingIds,
            @RequestParam(required = false) List<Integer> quantities,
            Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        List<CartItem> cartItems;
        if (pricingIds != null && !pricingIds.isEmpty()) {
            // "Mua ngay" hoặc chọn từ cart → build items từ DB
            cartItems = new ArrayList<>();
            for (int i = 0; i < pricingIds.size(); i++) {
                int qty = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1;
                variantPricingRepository.findById(pricingIds.get(i)).ifPresent(pricing -> {
                    CartItem item = new CartItem();
                    item.setPricingId(pricing.getId());
                    item.setUnitPrice(pricing.getPrice());
                    item.setOriginalPrice(pricing.getOriginalPrice());
                    item.setDurationLabel(pricing.getDurationLabel());
                    item.setQuantity(qty);
                    item.setSubtotal(pricing.getPrice().multiply(BigDecimal.valueOf(qty)));
                    if (pricing.getVariant() != null) {
                        item.setVariantId(pricing.getVariant().getId());
                        item.setVariantName(pricing.getVariant().getName());
                        if (pricing.getVariant().getProduct() != null) {
                            Product product = pricing.getVariant().getProduct();
                            item.setProductId(product.getId());
                            item.setProductName(product.getName());
                            item.setProductSlug(product.getSlug());
                            if (product.getImages() != null && !product.getImages().isEmpty()) {
                                item.setImageUrl(product.getImages().get(0).getImageUrl());
                            }
                        }
                    }
                    cartItems.add(item);
                });
            }
        } else {
            // Từ giỏ hàng → load hết cart
            @SuppressWarnings("unchecked")
            List<CartItem> allItems = (List<CartItem>) cartService.getCart(user.getId()).getOrDefault("items", Collections.emptyList());
            cartItems = allItems;
        }

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("pricingIds", cartItems.stream().map(CartItem::getPricingId).collect(Collectors.toList()));
        model.addAttribute("quantities", cartItems.stream().map(CartItem::getQuantity).collect(Collectors.toList()));
        BigDecimal subtotal = cartItems.stream()
                .filter(i -> i.getUnitPrice() != null)
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("nameBank","MB Bank");
        model.addAttribute("numberBank", "0379886918");
        model.addAttribute("nameUserBank", "Trần Thanh Vũ");
        return "user/checkout";
    }
    @PostMapping("/checkout")
    public ResponseEntity<PaymentMethod> processCheckout(
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String couponCode,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) List<Long> pricingIds,
            @RequestParam(required = false) List<Integer> quantities,
            RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        if (pricingIds == null || pricingIds.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm nào");
        }
        List<Map<String, Object>> items =  new ArrayList<>();
        for (int i = 0; i < pricingIds.size(); i++) {
            VariantPricing variantPricing = variantPricingRepository.findById(pricingIds.get(i))
                    .orElseThrow(() -> new RuntimeException("sản phẩm không tồn tại"));
            ProductVariant variant = variantPricing.getVariant();
            Product product = variant.getProduct();
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("variantId", variant.getId());
            item.put("pricingId", variantPricing.getId());
            item.put("unitPrice", variantPricing.getPrice());
            item.put("quantity", quantities.get(i));
            item.put("productName", product.getName());
            item.put("productSlug", product.getSlug());
            item.put("variantName", variant.getName());
            item.put("durationLabel", variantPricing.getDurationLabel());
            item.put("productImage", product.getImages().isEmpty() ? "" : product.getImages().get(0).getImageUrl());
            items.add(item);
        }
        Order order = orderService.createOrder(user, paymentMethod, couponCode, note, items);
        String url = "https://api-merchant.payos.vn/v2/payment-requests";
        if (paymentMethod != null && paymentMethod.equals("BANK_TRANSFER")) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("x-api-key", API_KEY);
            httpHeaders.set("x-client-id",  CLIENT_ID);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderCode(Math.toIntExact(order.getId()));
            BigDecimal totalAmount = order.getTotalAmount(); // DECIMAL(12,0)
            long amountLong = totalAmount.longValueExact(); // không có lẻ nên ok
            if (amountLong <= 0) throw new RuntimeException("amount invalid");
            if (amountLong > Integer.MAX_VALUE) {
                throw new RuntimeException("Amount too large for payOS (max ~2.1B VND)");
            }
            paymentRequest.setAmount((int) amountLong);
            paymentRequest.setDescription("DH " + order.getOrderNumber());
            paymentRequest.setCancelUrl("http://localhost:9090/orders/cancel");
            paymentRequest.setReturnUrl("http://localhost:9090/orders/" +  order.getOrderNumber());

            String buildDataToSign = buildDataToSign(paymentRequest.getAmount(), paymentRequest.getCancelUrl(), paymentRequest.getDescription(), paymentRequest.getOrderCode(), paymentRequest.getReturnUrl());
            paymentRequest.setSignature(hmacSha256Hex(buildDataToSign, CHECK_SUM_KEY));
            HttpEntity<PaymentRequest>  request = new HttpEntity<>(paymentRequest, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PaymentMethod>  response = restTemplate.exchange(url,  HttpMethod.POST, request, PaymentMethod.class);
            PaymentMethod body = response.getBody();
            System.out.println("PayOS response: " + body);
            System.out.println("PayOS code: " + (body != null ? body.getCode() : "null"));
            System.out.println("PayOS desc: " + (body != null ? body.getDesc() : "null"));
            System.out.println("PayOS data: " + (body != null ? body.getData() : "null"));
            if (body == null) throw new RuntimeException("payOS response null");
            if (body.getData() == null || body.getData().getCheckoutUrl() == null)
                throw new RuntimeException("checkoutUrl null, code=" + body.getCode() + ", desc=" + body.getDesc());
            return ResponseEntity.ok(body);
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/api/orders/{orderNumber}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable String orderNumber) {
        Order order = orderRepository.findById(Long.valueOf(orderNumber))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));
        return ResponseEntity.ok(Map.of("paymentStatus", order.getPaymentStatus(), "orderNumber", order.getOrderNumber()) );
    }
    @PostMapping("/webhook/payos")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> body) {
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        String receivedSignature = (String) body.get("signature");
        String dataToSign = "accountNumber=" + data.get("accountNumber")
                + "&amount=" + data.get("amount")
                + "&code=" + data.get("code")
                + "&counterAccountBankId=" + data.get("counterAccountBankId")
                + "&counterAccountBankName=" + data.get("counterAccountBankName")
                + "&counterAccountName=" + data.get("counterAccountName")
                + "&counterAccountNumber=" + data.get("counterAccountNumber")
                + "&currency=" + data.get("currency")
                + "&desc=" + data.get("desc")
                + "&description=" + data.get("description")
                + "&orderCode=" + data.get("orderCode")
                + "&paymentLinkId=" + data.get("paymentLinkId")
                + "&reference=" + data.get("reference")
                + "&transactionDateTime=" + data.get("transactionDateTime")
                + "&virtualAccountName=" + data.get("virtualAccountName")
                + "&virtualAccountNumber=" + data.get("virtualAccountNumber");

        String expectedSignature = hmacSha256Hex(dataToSign, CHECK_SUM_KEY);

        if (!expectedSignature.equals(receivedSignature)) {
            return ResponseEntity.status(403).body("Invalid signature");
        }
        String code = (String) data.get("code"); // "00" = thành công
        Long orderCode = Long.valueOf(data.get("orderCode").toString());

        if ("00".equals(code)) {
            Order order = orderRepository.findById(orderCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);
        }
        return ResponseEntity.ok().build();
    }
    // helper
    public static String buildDataToSign(long amount,
                                         String cancelUrl,
                                         String description,
                                         long orderCode,
                                         String returnUrl) {
        // đúng thứ tự alphabet theo docs: amount, cancelUrl, description, orderCode, returnUrl
        return "amount=" + amount
                + "&cancelUrl=" + cancelUrl
                + "&description=" + description
                + "&orderCode=" + orderCode
                + "&returnUrl=" + returnUrl;
    }
    public static String hmacSha256Hex(String data, String checksumKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // to hex lowercase
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign payOS request", e);
        }
    }
    /* ════════════════════════════════════
       ORDER DETAIL
       ════════════════════════════════════ */
    @GetMapping("/orders/{orderNumber}")
    public String orderDetail(@PathVariable String orderNumber, Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        Order order = orderService.getOrderDetail(user, orderNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        model.addAttribute("order", order);
        return "user/order-detail";
    }

    @PostMapping("/orders/{orderNumber}/cancel")
    public String cancelOrder(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        orderService.cancelOrder(user, orderNumber);
        redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng");
        return "redirect:/orders/" + orderNumber;
    }

    @PostMapping("/orders/{orderNumber}/confirm-payment")
    public String confirmPayment(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        try {
            orderService.confirmPayment(user, orderNumber);
            redirectAttributes.addFlashAttribute("success", "Thanh toán đã được xác nhận! Tài khoản đã được cấp phát.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + orderNumber;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        return "user/profile";
    }
    @PostMapping("/profile")
    public String profile(@RequestParam(required = false) String lastName, @RequestParam(required = false) String firstName, @RequestParam(required = false) String phone, RedirectAttributes redirectAttributes) {
        User user = securityUtils.getCurrentUser().orElseThrow(() -> new RuntimeException("Tài khoản chưa đăng nhập"));
        userService.updateProfile(user.getId(), lastName, firstName, phone);
        redirectAttributes.addFlashAttribute("success", "Cập nhập thông tin thành công");
        return "redirect:/profile";
    }

    /**
     * Auto-open: always create/get conversation with admin and show it directly.
     */
    @GetMapping("/messages")
    public String messages(Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        // Auto-create conversation with admin
        Conversation conv = chatService.getOrCreateConversation(user);
        model.addAttribute("selectedConv", conv);
        model.addAttribute("messages", messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId()));

        // Mark as read
        chatService.markAsRead(conv.getId(), Message.SenderType.USER);

        return "user/messages";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable Long id, Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        var conv = conversationRepository.findById(id).orElseThrow();
        model.addAttribute("selectedConv", conv);
        model.addAttribute("messages", messageRepository.findByConversationIdOrderByCreatedAtAsc(id));

        chatService.markAsRead(conv.getId(), Message.SenderType.USER);
        return "user/messages";
    }

    /**
     * Send message from user to admin.
     */
    @PostMapping("/messages/{id}/send")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        chatService.sendMessage(id, user.getId(), user.getUsername(), Message.SenderType.USER, content);
        return "redirect:/messages/" + id;
    }
}

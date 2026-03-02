package com.accountshop.controller.admin;

import com.accountshop.entity.*;
import com.accountshop.repository.CategoryRepository;
import com.accountshop.repository.DigitalAccountRepository;
import com.accountshop.repository.ProductRepository;
import com.accountshop.repository.VariantPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

/**
 * AdminProductController — admin product CRUD + digital account management.
 * Routes: /admin/products/**, /admin/pricing/**, /admin/accounts/**
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DigitalAccountRepository digitalAccountRepository;
    private final VariantPricingRepository variantPricingRepository;

    // ── Product CRUD ──

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Product> products;

        if ("ACTIVE".equals(status)) {
            products = productRepository.findByActiveTrue(pageable);
            model.addAttribute("statusFilter", "ACTIVE");
        } else if ("INACTIVE".equals(status)) {
            products = productRepository.findByActiveFalse(pageable);
            model.addAttribute("statusFilter", "INACTIVE");
        } else if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(search, pageable);
            model.addAttribute("searchTerm", search);
        } else {
            products = productRepository.findAll(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Quản lý Products");
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAsc());
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("pageTitle", "Sửa sản phẩm");
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAllByOrderByDisplayOrderAsc());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String detailDescription,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false, defaultValue = "false") Boolean active,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {

        Product product;
        if (id != null) {
            product = productRepository.findById(id).orElseThrow();
            product.getVariants().clear();
            product.getImages().clear();
        } else {
            product = new Product();
        }

        product.setName(name);
        product.setSlug(slug != null && !slug.isBlank() ? slug : generateSlug(name));
        product.setShortDescription(shortDescription);
        product.setDetailDescription(detailDescription);
        product.setActive(active);

        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(product::setCategory);
        }

        // Image
        if (imageUrl != null && !imageUrl.isBlank()) {
            ProductImage img = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .isPrimary(true)
                    .displayOrder(0)
                    .build();
            product.getImages().add(img);
        }

        // Parse variants from form params
        BigDecimal minPrice = null;
        int vIdx = 0;
        while (allParams.containsKey("variants[" + vIdx + "].name")) {
            String vName = allParams.get("variants[" + vIdx + "].name");
            String vDesc = allParams.get("variants[" + vIdx + "].description");

            ProductVariant variant = ProductVariant.builder()
                    .product(product)
                    .name(vName)
                    .description(vDesc)
                    .build();

            variant.setPricingList(new ArrayList<>());

            int pIdx = 0;
            while (allParams.containsKey("variants[" + vIdx + "].pricings[" + pIdx + "].price")) {
                String priceStr = allParams.get("variants[" + vIdx + "].pricings[" + pIdx + "].price");
                String durationStr = allParams.get("variants[" + vIdx + "].pricings[" + pIdx + "].durationMonths");
                String origPriceStr = allParams.get("variants[" + vIdx + "].pricings[" + pIdx + "].originalPrice");
                String stockStr = allParams.get("variants[" + vIdx + "].pricings[" + pIdx + "].availableStock");

                BigDecimal price = new BigDecimal(priceStr);
                VariantPricing pricing = VariantPricing.builder()
                        .variant(variant)
                        .durationMonths(durationStr != null && !durationStr.isBlank() ? Integer.parseInt(durationStr) : 1)
                        .durationLabel((durationStr != null && !durationStr.isBlank() ? durationStr : "1") + " tháng")
                        .price(price)
                        .originalPrice(origPriceStr != null && !origPriceStr.isBlank() ? new BigDecimal(origPriceStr) : null)
                        .availableStock(stockStr != null && !stockStr.isBlank() ? Integer.parseInt(stockStr) : 0)
                        .build();

                variant.getPricingList().add(pricing);
                if (minPrice == null || price.compareTo(minPrice) < 0) {
                    minPrice = price;
                }
                pIdx++;
            }

            product.getVariants().add(variant);
            vIdx++;
        }

        product.setMinPrice(minPrice);
        productRepository.save(product);

        redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được lưu thành công!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/toggle")
    public String toggleProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setActive(!product.getActive());
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", product.getActive() ? "Đã kích hoạt" : "Đã tạm ẩn");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm");
        return "redirect:/admin/products";
    }

    // ── Digital Accounts ──

    @GetMapping("/products/{id}/accounts")
    public String productAccounts(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Tài khoản — " + product.getName());

        List<Map<String, Object>> sections = new ArrayList<>();
        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                for (VariantPricing vp : v.getPricingList()) {
                    Map<String, Object> sec = new LinkedHashMap<>();
                    sec.put("variant", v);
                    sec.put("pricing", vp);
                    sec.put("accounts", digitalAccountRepository.findByVariantPricingId(vp.getId()));
                    sec.put("availableCount", digitalAccountRepository.countByVariantPricingIdAndStatus(vp.getId(), DigitalAccount.AccountStatus.AVAILABLE));
                    sec.put("allocatedCount", digitalAccountRepository.countByVariantPricingIdAndStatus(vp.getId(), DigitalAccount.AccountStatus.ALLOCATED));
                    sec.put("soldCount", digitalAccountRepository.countByVariantPricingIdAndStatus(vp.getId(), DigitalAccount.AccountStatus.SOLD));
                    sections.add(sec);
                }
            }
        }
        model.addAttribute("sections", sections);
        return "admin/product-accounts";
    }

    @PostMapping("/pricing/{id}/update-description")
    public String updatePricingDescription(
            @PathVariable Long id,
            @RequestParam String accountDescription,
            @RequestParam Long productId,
            RedirectAttributes redirectAttributes) {
        variantPricingRepository.findById(id).ifPresent(vp -> {
            vp.setAccountDescription(accountDescription);
            variantPricingRepository.save(vp);
        });
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật mô tả");
        return "redirect:/admin/products/" + productId + "/accounts";
    }

    @PostMapping("/accounts/save")
    public String saveAccount(
            @RequestParam Long variantPricingId,
            @RequestParam String accountInfo,
            @RequestParam Long productId,
            RedirectAttributes redirectAttributes) {

        VariantPricing vp = variantPricingRepository.findById(variantPricingId)
                .orElseThrow(() -> new RuntimeException("Pricing not found"));

        DigitalAccount da = DigitalAccount.builder()
                .variantPricing(vp)
                .accountInfo(accountInfo.trim())
                .status(DigitalAccount.AccountStatus.AVAILABLE)
                .build();
        digitalAccountRepository.save(da);

        vp.setAvailableStock(vp.getAvailableStock() + 1);
        variantPricingRepository.save(vp);

        redirectAttributes.addFlashAttribute("success", "Đã thêm tài khoản");
        return "redirect:/admin/products/" + productId + "/accounts";
    }

    @PostMapping("/accounts/bulk-add")
    public String bulkAddAccounts(
            @RequestParam Long variantPricingId,
            @RequestParam String bulkData,
            @RequestParam Long productId,
            RedirectAttributes redirectAttributes) {

        VariantPricing vp = variantPricingRepository.findById(variantPricingId)
                .orElseThrow(() -> new RuntimeException("Pricing not found"));

        String[] lines = bulkData.split("\\r?\\n");
        int count = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            DigitalAccount da = DigitalAccount.builder()
                    .variantPricing(vp)
                    .accountInfo(trimmed)
                    .status(DigitalAccount.AccountStatus.AVAILABLE)
                    .build();
            digitalAccountRepository.save(da);
            count++;
        }

        vp.setAvailableStock(vp.getAvailableStock() + count);
        variantPricingRepository.save(vp);

        redirectAttributes.addFlashAttribute("success", "Đã thêm " + count + " tài khoản");
        return "redirect:/admin/products/" + productId + "/accounts";
    }

    @PostMapping("/accounts/{id}/delete")
    public String deleteAccount(@PathVariable Long id,
                                @RequestParam Long productId,
                                RedirectAttributes redirectAttributes) {
        digitalAccountRepository.findById(id).ifPresent(da -> {
            if (da.getStatus() == DigitalAccount.AccountStatus.AVAILABLE) {
                VariantPricing vp = da.getVariantPricing();
                digitalAccountRepository.delete(da);
                vp.setAvailableStock(Math.max(0, vp.getAvailableStock() - 1));
                variantPricingRepository.save(vp);
            }
        });
        redirectAttributes.addFlashAttribute("success", "Đã xóa tài khoản");
        return "redirect:/admin/products/" + productId + "/accounts";
    }

    // ── Helpers ──

    private String generateSlug(String name) {
        return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("đ", "d").replaceAll("Đ", "D")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                + "-" + System.currentTimeMillis() % 10000;
    }
}

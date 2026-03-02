package com.accountshop.controller.user;

import com.accountshop.entity.User;
import com.accountshop.repository.CategoryRepository;
import com.accountshop.repository.ProductRepository;
import com.accountshop.repository.ProductQuestionRepository;
import com.accountshop.repository.ProductReviewRepository;
import com.accountshop.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * HomePageController — handles public-facing page views.
 * Routes: /, /home, /products, /products/{slug}, /cart, /dashboard
 */
@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductQuestionRepository productQuestionRepository;
    private final SecurityUtils securityUtils;

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

    @GetMapping("/cart")
    public String cart(Model model) {
        addCurrentUser(model);
        return "user/cart";
    }
}

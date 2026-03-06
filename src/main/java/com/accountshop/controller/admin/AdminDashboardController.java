package com.accountshop.controller.admin;

import com.accountshop.entity.Order;
import com.accountshop.repository.OrderRepository;
import com.accountshop.repository.ProductRepository;
import com.accountshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

/**
 * AdminDashboardController — admin dashboard with stats + charts.
 * Routes: GET /admin/dashboard
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private static final String[] MONTH_NAMES = {
        "", "T1", "T2", "T3", "T4", "T5", "T6",
        "T7", "T8", "T9", "T10", "T11", "T12"
    };

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // ── Stat cards ──
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        model.addAttribute("completedOrders", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
        model.addAttribute("totalRevenue", orderRepository.getTotalRevenue());

        // ── Chart data: last 12 months ──
        LocalDateTime twelveMonthsAgo = LocalDateTime.now().minusMonths(11).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Build ordered list of last 12 months (inclusive current)
        List<YearMonth> last12Months = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            last12Months.add(current.minusMonths(i));
        }

        // ── User Growth ──
        Map<String, Long> userMap = new LinkedHashMap<>();
        for (YearMonth ym : last12Months) {
            userMap.put(ym.getYear() + "-" + ym.getMonthValue(), 0L);
        }
        List<Object[]> userRows = userRepository.countNewUsersPerMonth(twelveMonthsAgo);
        for (Object[] row : userRows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            userMap.put(year + "-" + month, count);
        }

        List<String> userLabels = new ArrayList<>();
        List<Long> userData = new ArrayList<>();
        for (YearMonth ym : last12Months) {
            userLabels.add(MONTH_NAMES[ym.getMonthValue()] + "/" + (ym.getYear() % 100));
            userData.add(userMap.getOrDefault(ym.getYear() + "-" + ym.getMonthValue(), 0L));
        }

        // ── Revenue ──
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (YearMonth ym : last12Months) {
            revenueMap.put(ym.getYear() + "-" + ym.getMonthValue(), BigDecimal.ZERO);
        }
        List<Object[]> revenueRows = orderRepository.getRevenuePerMonth(twelveMonthsAgo);
        for (Object[] row : revenueRows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal amount = row[2] instanceof BigDecimal ? (BigDecimal) row[2]
                    : new BigDecimal(row[2].toString());
            revenueMap.put(year + "-" + month, amount);
        }

        List<String> revenueLabels = new ArrayList<>();
        List<BigDecimal> revenueData = new ArrayList<>();
        for (YearMonth ym : last12Months) {
            revenueLabels.add(MONTH_NAMES[ym.getMonthValue()] + "/" + (ym.getYear() % 100));
            revenueData.add(revenueMap.getOrDefault(ym.getYear() + "-" + ym.getMonthValue(), BigDecimal.ZERO));
        }

        model.addAttribute("userGrowthLabels", userLabels);
        model.addAttribute("userGrowthData", userData);
        model.addAttribute("revenueLabels", revenueLabels);
        model.addAttribute("revenueData", revenueData);

        return "admin/dashboard";
    }
}

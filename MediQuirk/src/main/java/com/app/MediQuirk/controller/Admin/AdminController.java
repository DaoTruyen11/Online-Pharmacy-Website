package com.app.MediQuirk.controller.Admin;

import com.app.MediQuirk.model.Orders;
import com.app.MediQuirk.services.OrderService;
import com.app.MediQuirk.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

//    @RequestMapping("/admin")
//    public String Admin(){
//
//        return "Admin/index";
//    }

    @RequestMapping("/admin")
    public String showStatistics(Model model) {
        int totalProductsSold = orderService.getTotalProductsSold();
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        long totalInvoices = orderService.getOrderCount();
        Map<String, Long> orderCountByStatus = orderService.getOrderCountByStatus();

        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("orderCountByStatus", orderCountByStatus);

        return "Admin/index";
    }

}

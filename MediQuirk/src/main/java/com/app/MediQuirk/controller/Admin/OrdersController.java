package com.app.MediQuirk.controller.Admin;


import com.app.MediQuirk.model.Orders;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String getOrderDashboard(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize);

        long orderCount = orderService.getOrderCount();
        Map<String, Long> statusCounts = orderService.getOrderCountByStatus();
        Page<Orders> orderPage = orderService.getAllOrdersPaginated(pageable);

        model.addAttribute("orderCount", orderCount);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("orderPage", orderPage);

        return "Admin/order/order-list";
    }

    @GetMapping("/detail/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        Orders order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "Admin/order/orderDetail";
    }


    @PostMapping("/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable String orderId, @RequestParam String newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        return "redirect:/admin/orders/" + orderId;
    }

}
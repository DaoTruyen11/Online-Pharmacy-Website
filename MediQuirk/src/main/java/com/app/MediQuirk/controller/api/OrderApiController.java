package com.app.MediQuirk.controller.api;


import com.app.MediQuirk.model.Orders;
import com.app.MediQuirk.services.OrderService;
import com.app.MediQuirk.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/count")
    public ResponseEntity<Long> getOrderCount() {
        long count = orderService.getOrderCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Long>> getOrderCountByStatus() {
        Map<String, Long> statusCounts = orderService.getOrderCountByStatus();
        return ResponseEntity.ok(statusCounts);
    }

    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders() {
        List<Orders> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
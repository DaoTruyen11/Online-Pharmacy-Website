package com.app.MediQuirk.controller.User;

import com.app.MediQuirk.model.Orders;
import com.app.MediQuirk.services.CartService;
import com.app.MediQuirk.services.MomoService;
import com.app.MediQuirk.services.OrderService;
import com.app.MediQuirk.services.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final MomoService momoService;
    private final VNPayService vnPayService;
    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public PaymentController(MomoService momoService, VNPayService vnPayService,
                             OrderService orderService, CartService cartService) {
        this.momoService = momoService;
        this.vnPayService = vnPayService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/momo-return")
    public String momoReturn(@RequestParam Map<String, String> queryParams, Model model) {
        logger.info("Received Momo return parameters: {}", queryParams);
        String errorCode = queryParams.get("errorCode");
        String orderId = queryParams.get("orderId");

        if (orderId == null) {
            return redirectWithError("/cart", "Order not found");
        }

        try {
            if (!momoService.verifySignature(queryParams)) {
                return redirectWithError("/cart", "Invalid signature");
            }

            Orders order = orderService.getOrderByOrderNumber(orderId);

            if ("0".equals(errorCode)) {
                return processSuccessfulPayment(orderId);
            } else {
                return redirectWithError("/cart", "Payment failed. Please try again.");
            }
        } catch (Exception e) {
            logger.error("Error processing Momo return", e);
            return redirectWithError("/cart", "Payment processing failed: " + e.getMessage());
        }
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> queryParams, Model model) {
        logger.info("Received VNPay return parameters: {}", queryParams);
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String orderNumber = queryParams.get("vnp_TxnRef");

        if (orderNumber == null) {
            return setModelAttributeAndReturnView(model, "Không tìm thấy thông tin đơn hàng.", "payment-failure");
        }

        try {
//            if (!vnPayService.verifyVnPayReturn(queryParams)) {
//                return setModelAttributeAndReturnView(model, "Invalid signature", "payment-failure");
//            }

            Orders order = orderService.getOrderByOrderNumber(orderNumber);

            if ("00".equals(vnp_ResponseCode)) {
                orderService.updateOrderStatus(orderNumber, "PAID");
                cartService.clearCart();
                return setModelAttributeAndReturnView(model, "Thanh toán thành công!", "payment-success");
            } else {
                return setModelAttributeAndReturnView(model, "Thanh toán thất bại. Vui lòng thử lại.", "payment-failure");
            }
        } catch (RuntimeException e) {
            logger.error("Error processing VNPay return", e);
            return setModelAttributeAndReturnView(model, "Không tìm thấy đơn hàng: " + e.getMessage(), "payment-failure");
        }
    }

    @GetMapping("/cod-success")
    public String codSuccess(@RequestParam("orderNumber") String orderNumber, Model model) {
        try {
            Optional<Orders> orderOptional = Optional.ofNullable(orderService.getOrderByOrderNumber(orderNumber));
            return orderOptional
                    .map(order -> {
                        model.addAttribute("order", order);
                        return "cod-success";
                    })
                    .orElseThrow(() -> new RuntimeException("Order not found"));
        } catch (RuntimeException e) {
            logger.error("Error processing COD success", e);
            model.addAttribute("error", "Không tìm thấy đơn hàng: " + e.getMessage());
            return "error-page";
        }
    }

    @GetMapping("/failure")
    public String paymentFailure(Model model) {
        return setModelAttributeAndReturnView(model, "Thanh toán thất bại. Vui lòng thử lại.", "payment-failure");
    }

    private String redirectWithError(String path, String error) {
        return "redirect:" + path + "?error=" + error;
    }

    private String setModelAttributeAndReturnView(Model model, String message, String viewName) {
        model.addAttribute("message", message);
        return viewName;
    }

    private String processSuccessfulPayment(String orderId) {
        orderService.updateOrderStatus(orderId, "PAID");
        cartService.clearCart();
        return "redirect:/cart/success?orderId=" + orderId;
    }
}
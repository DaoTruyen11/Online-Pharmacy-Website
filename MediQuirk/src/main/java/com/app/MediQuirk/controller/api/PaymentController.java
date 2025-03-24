//package com.app.MediQuirk.controller.api;
//
//import com.app.MediQuirk.services.MomoService;
//import com.app.MediQuirk.services.VNPayService;
//import com.app.MediQuirk.services.ZaloPayService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/payment")
//public class PaymentController {
//
//    private final MomoService momoService;
//    private final ZaloPayService zaloPayService;
//    private final VNPayService vnPayService;
//
//    public PaymentController(MomoService momoService, ZaloPayService zaloPayService, VNPayService vnPayService) {
//        this.momoService = momoService;
//        this.zaloPayService = zaloPayService;
//        this.vnPayService = vnPayService;
//    }
//
//    @PostMapping("/momo/create")
//    public ResponseEntity<MomoResponse> createMomoPayment(@RequestBody PaymentRequest request) {
//        try {
//            MomoResponse response = momoService.createPayment(request);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/zalopay/create")
//    public ResponseEntity<String> createZaloPayment(@RequestBody PaymentRequest request) {
//        try {
//            String paymentUrl = zaloPayService.createPayment(request);
//            return ResponseEntity.ok(paymentUrl);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/vnpay/create")
//    public ResponseEntity<String> createVNPayPayment(@RequestBody PaymentRequest request) {
//        try {
//            String paymentUrl = vnPayService.createPayment(request);
//            return ResponseEntity.ok(paymentUrl);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/vnpay-return")
//    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> queryParams) {
//        // Xử lý kết quả thanh toán từ VNPay
//        return ResponseEntity.ok("Payment processed");
//    }
//
//    // Các phương thức xử lý callback và thông báo thanh toán khác
//}
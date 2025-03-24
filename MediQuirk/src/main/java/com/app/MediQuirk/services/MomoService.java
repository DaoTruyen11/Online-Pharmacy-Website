package com.app.MediQuirk.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class MomoService {

    private final String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private final String partnerCode = "MOMOOJOI20210710";
    private final String accessKey = "iPXneGmrJH0G8FOP";
    private final String secretKey = "sFcbSGRSJjwGxwhhcEktCHWYUuTuPNDB";
    private final String returnUrl = "http://localhost:8080/payment/momo-return";
    private final String ipnUrl = "http://localhost:8080/payment/momo-ipn";

    @Autowired
    private CartService cartService;

    public String createPaymentUrl(String orderId, String orderInfo) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String requestId = String.valueOf(System.currentTimeMillis());
        long amount = cartService.getTotalPrice().setScale(0, RoundingMode.HALF_UP).longValue();
        String orderType = "momo_wallet";

        Map<String, String> rawData = new TreeMap<>();
        rawData.put("partnerCode", partnerCode);
        rawData.put("accessKey", accessKey);
        rawData.put("requestId", requestId);
        rawData.put("amount", String.valueOf(amount));
        rawData.put("orderId", orderId);
        rawData.put("orderInfo", orderInfo);
        rawData.put("returnUrl", returnUrl);
        rawData.put("notifyUrl", ipnUrl);
        rawData.put("extraData", "");
        rawData.put("requestType", orderType);

        StringBuilder rawSignature = new StringBuilder();
        for (Map.Entry<String, String> entry : rawData.entrySet()) {
            rawSignature.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        rawSignature.deleteCharAt(rawSignature.length() - 1);

        String signature = calculateHMAC(rawSignature.toString(), secretKey);

        rawData.put("signature", signature);

        StringBuilder paymentUrlBuilder = new StringBuilder(endpoint).append("?");
        for (Map.Entry<String, String> entry : rawData.entrySet()) {
            paymentUrlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                    .append("&");
        }
        paymentUrlBuilder.deleteCharAt(paymentUrlBuilder.length() - 1);

        return paymentUrlBuilder.toString();
    }

    private String calculateSignature(Map<String, String> rawData) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder signData = new StringBuilder();
        for (Map.Entry<String, String> entry : rawData.entrySet()) {
            signData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        signData.deleteCharAt(signData.length() - 1);
        return calculateHMAC(signData.toString(), secretKey);
    }
    private String calculateHMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public boolean verifySignature(Map<String, String> fields) throws NoSuchAlgorithmException, InvalidKeyException {
        String signatureReceived = fields.get("signature");
        fields.remove("signature");

        Map<String, String> sortedFields = new TreeMap<>(fields);

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            data.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        data.deleteCharAt(data.length() - 1);

        String signatureCalculated = calculateHMAC(data.toString(), secretKey);
        return signatureCalculated.equals(signatureReceived);
    }
}
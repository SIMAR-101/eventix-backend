package com.eventix.backend.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    // These annotations automatically pull your secret keys from application.properties!
    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            // 1. Get the amount from React (e.g., 500 rupees)
            int amount = Integer.parseInt(data.get("amount").toString());

            // 2. Initialize the Razorpay Client with your vault keys
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

            // 3. Build the official order request
            JSONObject orderRequest = new JSONObject();
            // Razorpay mathematically requires the amount in PAISE, not Rupees! (₹1 = 100 Paise)
            orderRequest.put("amount", amount * 100); 
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            // 4. Request the official Order ID from Razorpay's servers
            Order order = razorpayClient.orders.create(orderRequest);

            // 5. Send that secure Order ID back to React!
            return ResponseEntity.ok(order.toString());

        } catch (Exception e) {
            System.out.println("Razorpay Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to create payment order");
        }
    }
}
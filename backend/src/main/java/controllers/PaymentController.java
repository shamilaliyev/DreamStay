package controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @PostMapping("/mock")
    public ResponseEntity<?> processPayment(@RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> payload) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // Mock payment processing
        // Expects "amount", "propertyId" etc.
        System.out.println("Processing mock payment for user " + userId + ": " + payload);

        return ResponseEntity.ok(Map.of("status", "success", "transactionId", "MOCK-" + System.currentTimeMillis()));
    }
}

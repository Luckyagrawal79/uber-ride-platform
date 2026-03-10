package com.uber.paymentservice.strategy;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("walletProcessor")
public class WalletPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(Long rideId, Long passengerId, double amount) {
        // Simulate wallet deduction
        return PaymentResult.builder().success(true)
                .transactionId("WAL-" + UUID.randomUUID().toString().substring(0, 8)).build();
    }
}

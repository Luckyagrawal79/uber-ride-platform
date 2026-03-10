package com.uber.paymentservice.strategy;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("cashProcessor")
public class CashPaymentProcessor implements PaymentProcessor {
    
    @Override
    public PaymentResult process(Long rideId, Long passengerId, double amount) {
        // Cash is always successful - collected by driver
        return PaymentResult.builder().success(true)
                .transactionId("CASH-" + UUID.randomUUID().toString().substring(0, 8)).build();
    }
}

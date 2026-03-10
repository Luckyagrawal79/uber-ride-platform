package com.uber.paymentservice.strategy;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("creditCardProcessor")
public class CreditCardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult process(Long rideId, Long passengerId, double amount) {
        // Simulate credit card processing (would integrate with Stripe/Razorpay in production)
        return PaymentResult.builder().success(true)
                .transactionId("CC-" + UUID.randomUUID().toString().substring(0, 8)).build();
    }
}

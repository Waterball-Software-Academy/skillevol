package com.example.payment;

import java.math.BigDecimal;

public class StripePaymentGateway implements PaymentGateway {
    @Override
    public PaymentResult charge(PaymentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("amount must be positive");
        }
        return PaymentResult.success("stripe:" + request.getOrderId());
    }
}

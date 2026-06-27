package com.example.payment;

public interface PaymentGateway {
    PaymentResult charge(PaymentRequest request);
}

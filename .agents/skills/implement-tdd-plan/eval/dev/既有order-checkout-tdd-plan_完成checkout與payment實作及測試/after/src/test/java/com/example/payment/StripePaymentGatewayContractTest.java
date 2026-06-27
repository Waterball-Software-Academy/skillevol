package com.example.payment;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StripePaymentGatewayContractTest {
    @Test
    void chargeSucceedsForPositiveAmount() {
        StripePaymentGateway gateway = new StripePaymentGateway();

        PaymentResult result = gateway.charge(new PaymentRequest("order-1", "user-1", BigDecimal.ONE));

        assertTrue(result.isSuccess());
    }

    @Test
    void chargeFailsForNonPositiveAmount() {
        StripePaymentGateway gateway = new StripePaymentGateway();

        PaymentResult result = gateway.charge(new PaymentRequest("order-2", "user-2", BigDecimal.ZERO));

        assertFalse(result.isSuccess());
    }
}

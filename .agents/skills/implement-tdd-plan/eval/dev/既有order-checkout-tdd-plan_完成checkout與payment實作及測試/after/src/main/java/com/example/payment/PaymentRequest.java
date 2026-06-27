package com.example.payment;

import com.example.order.Order;
import java.math.BigDecimal;

public class PaymentRequest {
    private final String orderId;
    private final String userId;
    private final BigDecimal amount;

    public PaymentRequest(String orderId, String userId, BigDecimal amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public static PaymentRequest fromOrder(Order order) {
        return new PaymentRequest(order.getId(), order.getUserId(), order.getTotal());
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

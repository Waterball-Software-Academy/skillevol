package com.example.order;

import com.example.cart.Cart;
import com.example.payment.PaymentGateway;
import com.example.payment.PaymentRequest;
import com.example.payment.PaymentResult;

public class CheckoutService {
    private final OrderService orderService;
    private final PaymentGateway paymentGateway;

    public CheckoutService(OrderService orderService, PaymentGateway paymentGateway) {
        this.orderService = orderService;
        this.paymentGateway = paymentGateway;
    }

    public Order checkout(Cart cart) {
        Order order = orderService.createFromCart(cart);
        PaymentRequest request = PaymentRequest.fromOrder(order);
        PaymentResult result = paymentGateway.charge(request);
        if (!result.isSuccess()) {
            throw new IllegalStateException("Payment failed: " + result.getMessage());
        }
        return order;
    }
}

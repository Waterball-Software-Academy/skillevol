package com.example.order;

import com.example.cart.Cart;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderService {
    public Order createFromCart(Cart cart) {
        BigDecimal total = cart.getItems().stream()
            .map(item -> BigDecimal.valueOf(item.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Order(
            UUID.randomUUID().toString(),
            cart.getUserId(),
            total,
            Instant.now()
        );
    }
}

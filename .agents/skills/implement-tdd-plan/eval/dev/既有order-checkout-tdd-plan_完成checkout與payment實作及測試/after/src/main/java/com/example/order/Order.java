package com.example.order;

import com.example.cart.CartItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {
    private final String id;
    private final String userId;
    private final List<CartItem> items;
    private final BigDecimal total;
    private final Instant createdAt;

    public Order(String id, String userId, List<CartItem> items, BigDecimal total, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.items = List.copyOf(items);
        this.total = total;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

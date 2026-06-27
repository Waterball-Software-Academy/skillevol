package com.example.order;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {
    private final String id;
    private final String userId;
    private final BigDecimal total;
    private final Instant createdAt;

    public Order(String id, String userId, BigDecimal total, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

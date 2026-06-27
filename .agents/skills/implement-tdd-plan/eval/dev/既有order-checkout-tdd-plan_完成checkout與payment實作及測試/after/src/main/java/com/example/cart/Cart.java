package com.example.cart;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final String userId;
    private final List<CartItem> items = new ArrayList<>();

    public Cart(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }
}

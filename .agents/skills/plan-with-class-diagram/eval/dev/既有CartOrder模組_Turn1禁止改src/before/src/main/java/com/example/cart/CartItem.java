package com.example.cart;

public class CartItem {
    private final String sku;
    private final int quantity;

    public CartItem(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }
}

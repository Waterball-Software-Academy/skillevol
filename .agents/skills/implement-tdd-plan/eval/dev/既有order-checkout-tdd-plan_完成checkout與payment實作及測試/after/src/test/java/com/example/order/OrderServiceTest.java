package com.example.order;

import com.example.cart.Cart;
import com.example.cart.CartItem;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceTest {
    @Test
    void createFromCartCopiesUserAndItemsAndComputesTotal() {
        Cart cart = new Cart("user-1");
        cart.addItem(new CartItem("sku-1", 2));
        cart.addItem(new CartItem("sku-2", 1));

        Order order = new OrderService().createFromCart(cart);

        assertEquals("user-1", order.getUserId());
        assertEquals(2, order.getItems().size());
        assertEquals("sku-1", order.getItems().get(0).getSku());
        assertEquals(BigDecimal.valueOf(3), order.getTotal());
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedAt());
    }
}

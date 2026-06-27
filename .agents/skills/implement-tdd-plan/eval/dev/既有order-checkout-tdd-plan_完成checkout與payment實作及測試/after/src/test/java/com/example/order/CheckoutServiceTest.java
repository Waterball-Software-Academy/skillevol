package com.example.order;

import com.example.cart.Cart;
import com.example.cart.CartItem;
import com.example.payment.PaymentGateway;
import com.example.payment.PaymentRequest;
import com.example.payment.PaymentResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutServiceTest {
    @Test
    void checkoutCreatesOrderChargesGatewayAndReturnsOrder() {
        Cart cart = new Cart("user-1");
        cart.addItem(new CartItem("sku-1", 2));

        SpyOrderService orderService = new SpyOrderService(
            new Order("order-1", "user-1", List.of(new CartItem("sku-1", 2)), BigDecimal.valueOf(2), Instant.parse("2024-01-01T00:00:00Z"))
        );
        CapturingGateway gateway = new CapturingGateway(PaymentResult.success("ok"));
        CheckoutService checkoutService = new CheckoutService(orderService, gateway);

        Order result = checkoutService.checkout(cart);

        assertTrue(orderService.called);
        assertSame(result, orderService.orderToReturn);
        assertEquals("order-1", gateway.lastRequest.getOrderId());
        assertEquals("user-1", gateway.lastRequest.getUserId());
        assertEquals(BigDecimal.valueOf(2), gateway.lastRequest.getAmount());
    }

    @Test
    void checkoutBuildsPaymentRequestAtTheBoundary() {
        SpyOrderService orderService = new SpyOrderService(
            new Order("order-9", "user-9", List.of(new CartItem("sku-x", 4)), BigDecimal.valueOf(4), Instant.parse("2024-01-02T00:00:00Z"))
        );
        CapturingGateway gateway = new CapturingGateway(PaymentResult.success("ok"));
        CheckoutService checkoutService = new CheckoutService(orderService, gateway);

        checkoutService.checkout(new Cart("user-9"));

        assertEquals("order-9", gateway.lastRequest.getOrderId());
        assertEquals("user-9", gateway.lastRequest.getUserId());
        assertEquals(BigDecimal.valueOf(4), gateway.lastRequest.getAmount());
    }

    @Test
    void checkoutThrowsWhenGatewayFails() {
        SpyOrderService orderService = new SpyOrderService(
            new Order("order-2", "user-2", List.of(new CartItem("sku-2", 1)), BigDecimal.ONE, Instant.parse("2024-01-03T00:00:00Z"))
        );
        CapturingGateway gateway = new CapturingGateway(PaymentResult.failure("card declined"));
        CheckoutService checkoutService = new CheckoutService(orderService, gateway);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> checkoutService.checkout(new Cart("user-2")));

        assertEquals("Payment failed: card declined", error.getMessage());
    }

    private static final class SpyOrderService extends OrderService {
        private final Order orderToReturn;
        private boolean called;

        private SpyOrderService(Order orderToReturn) {
            this.orderToReturn = orderToReturn;
        }

        @Override
        public Order createFromCart(Cart cart) {
            called = true;
            return orderToReturn;
        }
    }

    private static final class CapturingGateway implements PaymentGateway {
        private final PaymentResult resultToReturn;
        private PaymentRequest lastRequest;

        private CapturingGateway(PaymentResult resultToReturn) {
            this.resultToReturn = resultToReturn;
        }

        @Override
        public PaymentResult charge(PaymentRequest request) {
            lastRequest = request;
            return resultToReturn;
        }
    }
}

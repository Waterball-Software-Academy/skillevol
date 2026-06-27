package com.example.miniweb.routing;

import com.example.miniweb.handler.RequestHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouterTest {
    @Test
    void matchResolvesStaticPathAndPathParameter() {
        Router router = new Router();
        RequestHandler usersHandler = context -> { };
        RequestHandler healthHandler = context -> { };
        router.register("GET", "/users/{id}", usersHandler);
        router.register("GET", "/health", healthHandler);

        RouteMatch userMatch = router.match("GET", "/users/42");
        RouteMatch healthMatch = router.match("GET", "/health");

        assertTrue(userMatch.isHit());
        assertSame(usersHandler, userMatch.getHandler());
        assertEquals("42", userMatch.getPathParams().get("id"));
        assertTrue(healthMatch.isHit());
        assertSame(healthHandler, healthMatch.getHandler());
    }

    @Test
    void matchMissesWhenMethodOrPathDoesNotMatch() {
        Router router = new Router();
        router.register("GET", "/users/{id}", context -> { });

        assertFalse(router.match("POST", "/users/42").isHit());
        assertFalse(router.match("GET", "/teams/42").isHit());
    }
}

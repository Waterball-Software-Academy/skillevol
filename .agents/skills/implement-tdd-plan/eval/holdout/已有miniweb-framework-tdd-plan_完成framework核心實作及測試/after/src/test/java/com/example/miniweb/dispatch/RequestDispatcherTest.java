package com.example.miniweb.dispatch;

import com.example.miniweb.handler.RequestHandler;
import com.example.miniweb.http.RequestContext;
import com.example.miniweb.http.ResponseWriter;
import com.example.miniweb.middleware.Middleware;
import com.example.miniweb.routing.Router;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestDispatcherTest {
    @Test
    void dispatchRunsRoutingMiddlewareAndHandler() {
        Router router = new Router();
        List<String> trace = new ArrayList<>();
        router.register("GET", "/users/{id}", context -> {
            trace.add("handler:" + context.getPathParam("id"));
            context.getResponseWriter().writeText(200, "ok");
        });
        Middleware middleware = (context, chain) -> {
            trace.add("middleware-before");
            chain.next(context);
            trace.add("middleware-after");
        };

        ResponseWriter writer = new ResponseWriter();
        RequestDispatcher dispatcher = new RequestDispatcher(router, List.of(middleware));
        dispatcher.dispatch(new RequestContext("GET", "/users/42", writer));

        assertEquals(List.of("middleware-before", "handler:42", "middleware-after"), trace);
        assertEquals(200, writer.getStatus());
        assertEquals("ok", writer.getBody());
    }

    @Test
    void dispatchWrites404WhenRouteMisses() {
        List<String> trace = new ArrayList<>();
        Middleware middleware = (context, chain) -> trace.add("middleware");
        RequestHandler handler = context -> trace.add("handler");

        Router router = new Router();
        router.register("GET", "/health", handler);
        ResponseWriter writer = new ResponseWriter();
        RequestDispatcher dispatcher = new RequestDispatcher(router, List.of(middleware));

        dispatcher.dispatch(new RequestContext("GET", "/users/42", writer));

        assertEquals(List.of(), trace);
        assertEquals(404, writer.getStatus());
        assertEquals("Not Found", writer.getBody());
    }

    @Test
    void dispatchMapsHandlerExceptionTo500() {
        Router router = new Router();
        router.register("GET", "/boom", context -> {
            throw new IllegalStateException("boom");
        });

        ResponseWriter writer = new ResponseWriter();
        RequestDispatcher dispatcher = new RequestDispatcher(router, List.of());
        dispatcher.dispatch(new RequestContext("GET", "/boom", writer));

        assertEquals(500, writer.getStatus());
        assertEquals("Internal Server Error", writer.getBody());
    }
}

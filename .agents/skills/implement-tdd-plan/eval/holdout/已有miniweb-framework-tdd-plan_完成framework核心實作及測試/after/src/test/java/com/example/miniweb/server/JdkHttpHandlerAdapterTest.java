package com.example.miniweb.server;

import com.example.miniweb.dispatch.RequestDispatcher;
import com.example.miniweb.http.RequestContext;
import com.example.miniweb.middleware.Middleware;
import com.example.miniweb.routing.Router;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdkHttpHandlerAdapterTest {
    @Test
    void handleBuildsFrameworkContextAndWritesResponse() throws Exception {
        Router router = new Router();
        router.register("GET", "/health", context -> context.getResponseWriter().writeText(200, "ok"));
        RequestDispatcher dispatcher = new RequestDispatcher(router, List.of());
        JdkHttpHandlerAdapter adapter = new JdkHttpHandlerAdapter(dispatcher);
        StubHttpExchange exchange = new StubHttpExchange("GET", "/health");

        adapter.handle(exchange);

        assertEquals(200, exchange.getSentStatus());
        assertEquals("ok", exchange.getSentBody());
    }

    @Test
    void handleReturns500WhenFrameworkDispatchMapsFailure() throws Exception {
        Router router = new Router();
        router.register("GET", "/boom", context -> {
            throw new IllegalStateException("boom");
        });
        Middleware passThrough = (RequestContext context, com.example.miniweb.middleware.MiddlewareChain chain) -> chain.next(context);
        RequestDispatcher dispatcher = new RequestDispatcher(router, List.of(passThrough));
        JdkHttpHandlerAdapter adapter = new JdkHttpHandlerAdapter(dispatcher);
        StubHttpExchange exchange = new StubHttpExchange("GET", "/boom");

        adapter.handle(exchange);

        assertEquals(500, exchange.getSentStatus());
        assertEquals("Internal Server Error", exchange.getSentBody());
    }

    private static final class StubHttpExchange extends HttpExchange {
        private final Headers requestHeaders = new Headers();
        private final Headers responseHeaders = new Headers();
        private final URI requestUri;
        private final String requestMethod;
        private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        private int sentStatus;

        private StubHttpExchange(String requestMethod, String path) {
            this.requestMethod = requestMethod;
            this.requestUri = URI.create(path);
        }

        private int getSentStatus() {
            return sentStatus;
        }

        private String getSentBody() {
            return responseBody.toString();
        }

        @Override
        public Headers getRequestHeaders() {
            return requestHeaders;
        }

        @Override
        public Headers getResponseHeaders() {
            return responseHeaders;
        }

        @Override
        public URI getRequestURI() {
            return requestUri;
        }

        @Override
        public String getRequestMethod() {
            return requestMethod;
        }

        @Override
        public HttpContext getHttpContext() {
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getRequestBody() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public OutputStream getResponseBody() {
            return responseBody;
        }

        @Override
        public void sendResponseHeaders(int rCode, long responseLength) {
            this.sentStatus = rCode;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress("127.0.0.1", 12345);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress("127.0.0.1", 8080);
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
        }

        @Override
        public void setStreams(InputStream i, OutputStream o) {
        }

        @Override
        public HttpPrincipal getPrincipal() {
            return null;
        }
    }
}

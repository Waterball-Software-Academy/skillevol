package com.example.miniweb.server;

import com.example.miniweb.dispatch.RequestDispatcher;
import com.example.miniweb.http.RequestContext;
import com.example.miniweb.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JdkHttpHandlerAdapter {
    private final RequestDispatcher dispatcher;

    public JdkHttpHandlerAdapter(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void handle(HttpExchange exchange) throws IOException {
        ResponseWriter writer = new ResponseWriter();
        RequestContext context = new RequestContext(
            exchange.getRequestMethod(),
            exchange.getRequestURI().getPath(),
            writer
        );

        dispatcher.dispatch(context);

        byte[] body = writer.getBody().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(writer.getStatus(), body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}

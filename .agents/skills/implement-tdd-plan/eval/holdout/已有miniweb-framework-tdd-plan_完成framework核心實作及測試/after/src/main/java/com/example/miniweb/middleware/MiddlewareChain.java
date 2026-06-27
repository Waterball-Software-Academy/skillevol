package com.example.miniweb.middleware;

import com.example.miniweb.handler.RequestHandler;
import com.example.miniweb.http.RequestContext;
import java.util.List;

public class MiddlewareChain {
    private final List<Middleware> middlewares;
    private final RequestHandler terminalHandler;
    private final int index;

    public MiddlewareChain(List<Middleware> middlewares, RequestHandler terminalHandler) {
        this(middlewares, terminalHandler, 0);
    }

    private MiddlewareChain(List<Middleware> middlewares, RequestHandler terminalHandler, int index) {
        this.middlewares = List.copyOf(middlewares);
        this.terminalHandler = terminalHandler;
        this.index = index;
    }

    public void next(RequestContext context) {
        if (index >= middlewares.size()) {
            terminalHandler.handle(context);
            return;
        }
        Middleware current = middlewares.get(index);
        current.intercept(context, new MiddlewareChain(middlewares, terminalHandler, index + 1));
    }
}

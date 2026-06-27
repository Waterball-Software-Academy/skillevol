package com.example.miniweb.middleware;

import com.example.miniweb.http.RequestContext;

@FunctionalInterface
public interface Middleware {
    void intercept(RequestContext context, MiddlewareChain chain);
}

package com.example.miniweb.handler;

import com.example.miniweb.http.RequestContext;

@FunctionalInterface
public interface RequestHandler {
    void handle(RequestContext context);
}

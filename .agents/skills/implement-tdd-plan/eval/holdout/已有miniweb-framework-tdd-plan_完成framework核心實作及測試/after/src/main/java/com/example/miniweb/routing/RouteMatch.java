package com.example.miniweb.routing;

import com.example.miniweb.handler.RequestHandler;
import java.util.Collections;
import java.util.Map;

public class RouteMatch {
    private final boolean hit;
    private final RequestHandler handler;
    private final Map<String, String> pathParams;

    private RouteMatch(boolean hit, RequestHandler handler, Map<String, String> pathParams) {
        this.hit = hit;
        this.handler = handler;
        this.pathParams = pathParams;
    }

    public static RouteMatch hit(RequestHandler handler, Map<String, String> pathParams) {
        return new RouteMatch(true, handler, Map.copyOf(pathParams));
    }

    public static RouteMatch miss() {
        return new RouteMatch(false, null, Collections.emptyMap());
    }

    public boolean isHit() {
        return hit;
    }

    public RequestHandler getHandler() {
        return handler;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }
}

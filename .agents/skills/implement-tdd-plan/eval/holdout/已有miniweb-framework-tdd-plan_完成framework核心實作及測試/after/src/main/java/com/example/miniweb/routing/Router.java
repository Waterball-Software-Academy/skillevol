package com.example.miniweb.routing;

import com.example.miniweb.handler.RequestHandler;
import java.util.LinkedHashMap;
import java.util.Map;

public class Router {
    private final RouteRegistry routeRegistry;

    public Router() {
        this(new RouteRegistry());
    }

    public Router(RouteRegistry routeRegistry) {
        this.routeRegistry = routeRegistry;
    }

    public void register(String method, String path, RequestHandler handler) {
        routeRegistry.register(method, path, handler);
    }

    public RouteMatch match(String method, String path) {
        for (RouteRegistry.RouteDefinition route : routeRegistry.getRoutes()) {
            if (!route.getMethod().equalsIgnoreCase(method)) {
                continue;
            }
            Map<String, String> pathParams = matchPath(route.getPathPattern(), path);
            if (pathParams != null) {
                return RouteMatch.hit(route.getHandler(), pathParams);
            }
        }
        return RouteMatch.miss();
    }

    private Map<String, String> matchPath(String pathPattern, String actualPath) {
        String[] expectedSegments = segments(pathPattern);
        String[] actualSegments = segments(actualPath);
        if (expectedSegments.length != actualSegments.length) {
            return null;
        }

        Map<String, String> pathParams = new LinkedHashMap<>();
        for (int i = 0; i < expectedSegments.length; i++) {
            String expected = expectedSegments[i];
            String actual = actualSegments[i];
            if (expected.startsWith("{") && expected.endsWith("}")) {
                pathParams.put(expected.substring(1, expected.length() - 1), actual);
                continue;
            }
            if (!expected.equals(actual)) {
                return null;
            }
        }
        return pathParams;
    }

    private String[] segments(String path) {
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        if (normalized.isEmpty()) {
            return new String[0];
        }
        return normalized.split("/");
    }
}

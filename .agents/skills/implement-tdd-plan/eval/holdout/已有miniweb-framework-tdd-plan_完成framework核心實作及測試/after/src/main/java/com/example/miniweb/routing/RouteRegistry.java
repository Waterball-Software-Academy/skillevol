package com.example.miniweb.routing;

import com.example.miniweb.handler.RequestHandler;
import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {
    private final List<RouteDefinition> routes = new ArrayList<>();

    public void register(String method, String pathPattern, RequestHandler handler) {
        routes.add(new RouteDefinition(method, pathPattern, handler));
    }

    public List<RouteDefinition> getRoutes() {
        return List.copyOf(routes);
    }

    public static class RouteDefinition {
        private final String method;
        private final String pathPattern;
        private final RequestHandler handler;

        public RouteDefinition(String method, String pathPattern, RequestHandler handler) {
            this.method = method;
            this.pathPattern = pathPattern;
            this.handler = handler;
        }

        public String getMethod() {
            return method;
        }

        public String getPathPattern() {
            return pathPattern;
        }

        public RequestHandler getHandler() {
            return handler;
        }
    }
}

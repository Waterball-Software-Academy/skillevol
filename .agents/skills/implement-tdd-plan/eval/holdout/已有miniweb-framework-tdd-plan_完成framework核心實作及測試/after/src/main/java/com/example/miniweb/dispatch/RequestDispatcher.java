package com.example.miniweb.dispatch;

import com.example.miniweb.http.RequestContext;
import com.example.miniweb.middleware.Middleware;
import com.example.miniweb.middleware.MiddlewareChain;
import com.example.miniweb.routing.RouteMatch;
import com.example.miniweb.routing.Router;
import java.util.List;

public class RequestDispatcher {
    private final Router router;
    private final List<Middleware> middlewares;

    public RequestDispatcher(Router router, List<Middleware> middlewares) {
        this.router = router;
        this.middlewares = List.copyOf(middlewares);
    }

    public void dispatch(RequestContext context) {
        try {
            RouteMatch routeMatch = router.match(context.getMethod(), context.getPath());
            if (!routeMatch.isHit()) {
                context.getResponseWriter().writeText(404, "Not Found");
                return;
            }

            RequestContext routedContext = context.withPathParams(routeMatch.getPathParams());
            MiddlewareChain chain = new MiddlewareChain(middlewares, routeMatch.getHandler());
            chain.next(routedContext);
        } catch (RuntimeException error) {
            context.getResponseWriter().writeText(500, "Internal Server Error");
        }
    }
}

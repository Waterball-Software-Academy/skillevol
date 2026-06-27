package com.example.miniweb.middleware;

import com.example.miniweb.http.RequestContext;
import com.example.miniweb.http.ResponseWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiddlewareChainTest {
    @Test
    void nextWrapsMiddlewaresInRegistrationOrder() {
        List<String> trace = new ArrayList<>();
        Middleware first = (context, chain) -> {
            trace.add("m1-before");
            chain.next(context);
            trace.add("m1-after");
        };
        Middleware second = (context, chain) -> {
            trace.add("m2-before");
            chain.next(context);
            trace.add("m2-after");
        };

        MiddlewareChain chain = new MiddlewareChain(List.of(first, second), context -> trace.add("handler"));
        chain.next(new RequestContext("GET", "/users/42", new ResponseWriter()));

        assertEquals(List.of("m1-before", "m2-before", "handler", "m2-after", "m1-after"), trace);
    }

    @Test
    void nextShortCircuitsWhenMiddlewareDoesNotContinue() {
        List<String> trace = new ArrayList<>();
        Middleware stop = (context, chain) -> trace.add("stop");
        Middleware skipped = (context, chain) -> trace.add("skipped");

        MiddlewareChain chain = new MiddlewareChain(List.of(stop, skipped), context -> trace.add("handler"));
        chain.next(new RequestContext("GET", "/users/42", new ResponseWriter()));

        assertEquals(List.of("stop"), trace);
    }
}

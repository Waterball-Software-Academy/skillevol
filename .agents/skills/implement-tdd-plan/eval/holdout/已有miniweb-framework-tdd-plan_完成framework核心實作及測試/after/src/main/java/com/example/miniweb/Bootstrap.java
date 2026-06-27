package com.example.miniweb;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 * 最小啟動骨架：直接用 JDK HttpServer + inline request handling。
 * 目標：在此之上建構 MiniWeb framework 組件（見 docs/miniweb-requirements.md）。
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            byte[] body = "ok".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        System.out.println("HttpServer listening on :8080");
    }
}

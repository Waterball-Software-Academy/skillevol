package com.example.auth;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 * 最小 auth 啟動骨架：目前把 auth/session 判斷直接寫在 JDK handler 裡。
 * 目標：演進成可重用的 Auth / Session 組件分工。
 */
public class AuthBootstrap {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/me", exchange -> {
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
        });
        server.start();
        System.out.println("Auth server listening on :8081");
    }
}
package com.example.auth;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 * 最小 auth 啟動骨架：目前把 auth/session 判斷直接寫在 JDK handler 裡。
 * 目標：演進成可重用的 Auth / Session 組件分工。
 */
public class AuthBootstrap {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/me", exchange -> {
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
        });
        server.start();
        System.out.println("Auth server listening on :8081");
    }
}

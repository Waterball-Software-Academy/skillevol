package com.example.rpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 最小啟動骨架：直接用 ServerSocket + inline dispatch。
 * 目標：在此之上建構可重用的 RPC framework（見 docs/rpc-server-requirements.md）。
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    System.out.println("decode frame from " + socket.getRemoteSocketAddress());
                    System.out.println("dispatch method inline");
                    System.out.println("write response inline");
                } catch (RuntimeException ex) {
                    System.out.println("handle error inline");
                } finally {
                    socket.close();
                }
            }
        }
    }
}

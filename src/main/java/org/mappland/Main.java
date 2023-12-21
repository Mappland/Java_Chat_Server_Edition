package org.mappland;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.net.httpserver.HttpServer;

import org.mappland.Handler.*;
import org.mappland.function.Config;

public class Main {
    public static Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.info("Server starting...");

        // 启动HTTP服务器
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(config.user_port), 0);
        httpServer.createContext("/user_create", new Handler_Create_User());
        logger.info("Method " + "/user_create" + " load success");
        httpServer.createContext("/user_verify", new Handler_Verify_User());
        logger.info("Method " + "/user_verify" + " load success");
        httpServer.setExecutor(null); // 创建默认执行器
        httpServer.start();
        logger.info("HTTP Server started on port 8080");

        // 启动聊天服务器
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(config.chat_port);
                logger.info("Chat Server is running on port 12345...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    Handler_Chat handler = new Handler_Chat(clientSocket);
                    handler.start();
                }
            } catch (IOException e) {
                logger.error("Error starting Chat Server: " + e.getMessage(), e);
            }
        }).start();
    }
}

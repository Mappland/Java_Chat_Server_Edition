package org.mappland.function;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseSender {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseSender.class);
    public static void sendErrorResponse(HttpExchange exchange, String message, int statusCode) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("code", statusCode);
        responseJson.addProperty("message", message);

        sendJsonResponse(exchange, responseJson, statusCode);
    }

    public static void sendResponse(HttpExchange exchange, String message, int statusCode) throws IOException {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("code", statusCode);
        responseJson.addProperty("message", message);

        sendJsonResponse(exchange, responseJson, statusCode);
    }

    public static void sendJsonResponse(HttpExchange exchange, JsonObject responseJson, int statusCode) throws IOException {
        // 将JsonObject转换为字符串
        String responseString = responseJson.toString();

        // 将字符串转换为字节
        byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);

        // 设置响应头
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        // 发送响应体
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}

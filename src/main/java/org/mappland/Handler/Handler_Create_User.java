package org.mappland.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mappland.ProException.UserClassException;
import org.mappland.chat_class.User;
import org.mappland.function.HttpResponseSender;
import org.mappland.function.JsonHandle;

import java.io.IOException;


/**
 * 该类实现了 HttpHandler 接口。
 * 用于处理创建用户的 HTTP 请求。
 * 它定义了如何接收和处理来自客户端的创建用户请求。
 */
public class Handler_Create_User implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(Handler_Create_User.class);


    /**
     * 处理 HTTP 请求以创建新用户。
     * 这个方法首先检查请求方法是否为 POST，然后解析请求体中的 JSON 数据，
     * 并在满足所有要求后调用 {@code create_user} 方法来创建新用户。
     *
     * @param exchange HttpExchange 对象，包含了请求和响应的所有信息。
     * @throws IOException 如果处理请求时发生 I/O 错误。
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            HttpResponseSender.sendErrorResponse(exchange, "方法不被允许", 405);
            return;
        }

        logger.info("Received POST request for create user: " + exchange.getRequestURI());

        try {
            JsonObject ask_json = JsonHandle.result_handle(exchange);
            String[] attributes = {"username", "password"};
            if (!JsonHandle.attributes_in(ask_json, attributes)) {
                HttpResponseSender.sendErrorResponse(exchange, "请求体格式不正确", 400);
                return;
            }

            String username = ask_json.get("username").getAsString();
            String userPassword = ask_json.get("password").getAsString();
            create_user(exchange, username, userPassword);

        } catch (IOException e) {
            HttpResponseSender.sendErrorResponse(exchange, "请求体未能正确识别", 400);
        }
    }


    /**
     * 创建新用户的具体实现方法。
     * 这个方法接收用户名和密码，然后尝试创建新用户。
     * 如果用户创建成功，发送成功响应；如果发生错误，发送相应的错误响应。
     *
     * @param t HttpExchange 对象，用于发送响应。
     * @param username String类 用户名。
     * @param userPassword String类 用户密码。
     * @throws IOException 如果发送响应时发生 I/O 错误。
     */
    private void create_user(HttpExchange t, String username, String userPassword) throws IOException {
        try {
            User.create_user(username, userPassword);
            HttpResponseSender.sendResponse(t, "用户创建成功", 200);
            logger.info("用户创建成功");

        } catch (UserClassException.PasswordIllegal e) {
            HttpResponseSender.sendErrorResponse(t, "密码不合法", 402);
            logger.info("密码不合法");

        } catch (UserClassException.UserExist e) {
            HttpResponseSender.sendErrorResponse(t, "用户已存在", 401);
            logger.info("用户已经存在");

        } catch (UserClassException e) {
            HttpResponseSender.sendErrorResponse(t, "发生其他异常", 500);
            logger.error("UserClassException caught: ", e);
        }
    }
}

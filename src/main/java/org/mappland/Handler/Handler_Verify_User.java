package org.mappland.Handler;

import org.mappland.ProException.JwtException;
import org.mappland.ProException.UserClassException;
import org.mappland.chat_class.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mappland.function.HttpResponseSender;
import org.mappland.function.JsonHandle;

import java.io.IOException;
import java.sql.SQLException;


public class Handler_Verify_User implements HttpHandler{
    private static final Logger logger = LoggerFactory.getLogger(Handler_Verify_User.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod()))
        {
            HttpResponseSender.sendErrorResponse(exchange, "方法不被允许", 405);
            return;
        }

        logger.info("Received GET request for verify user: " + exchange.getRequestURI());

        try{
            JsonObject ask_json = JsonHandle.result_handle(exchange);
            String[] attributes = {"username", "password"};
            if (!JsonHandle.attributes_in(ask_json, attributes)) {
                HttpResponseSender.sendErrorResponse(exchange, "请求体格式不正确", 400);
                return;
            }
            String username = ask_json.get("username").getAsString();
            String userPassword = ask_json.get("password").getAsString();
            User user_1 = new User(username, userPassword);

            try{
               if (user_1.verify_user()) {
                   // 创建返回json文件
                   JsonObject responseJson = new JsonObject();
                   responseJson.addProperty("code", 200);
                   responseJson.addProperty("message", "用户验证成功");
                   responseJson.addProperty("jwt", user_1.user_jwt.toString());

                   // 发送返回json文件
                   HttpResponseSender.sendJsonResponse(exchange, responseJson, 200);
                   logger.info(username + "验证成功");
               }
               else{
                   // 创建返回json文件
                   JsonObject responseJson = new JsonObject();
                   responseJson.addProperty("code", 401);
                   responseJson.addProperty("message", "密码错误");

                   // 发送返回json文件
                   HttpResponseSender.sendJsonResponse(exchange, responseJson, 401);
                   logger.info(username + "密码错误");
               }

            } catch (JwtException.CreateError ex) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("code", 501);
                responseJson.addProperty("message", username + " 的Jwt创建错误");

                // 发送返回json文件
                HttpResponseSender.sendJsonResponse(exchange, responseJson, 501);
                logger.error(username + " 的Jwt创建错误");

            } catch (JwtException e) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("code", 501);
                responseJson.addProperty("message", username + " 的Jwt生成错误");

                // 发送返回json文件
                HttpResponseSender.sendJsonResponse(exchange, responseJson, 501);


            } catch (UserClassException.NotFound e) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("code", 404);
                responseJson.addProperty("message", username + " 不存在");

                // 发送返回json文件
                HttpResponseSender.sendJsonResponse(exchange, responseJson, 405);
            } catch (SQLException e) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("code", 500);
                responseJson.addProperty("message", "服务器内部数据库错误");

                // 发送返回json文件
                HttpResponseSender.sendJsonResponse(exchange, responseJson, 500);
            }

        } catch (IOException error) {
            HttpResponseSender.sendErrorResponse(exchange, "请求体未能正确识别", 400);
        }
    }
}
package org.mappland.Handler;

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.SQLException;

import com.google.gson.JsonObject;

import org.mappland.function.JDBC;
import org.mappland.function.JsonHandle;
import org.mappland.function.Jwt;
import org.mappland.ProException.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler_Chat extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Handler_Chat.class);
    private final Socket socket;
    private PrintWriter out;
    private static final Map<String, Set<Handler_Chat>> chatRooms = new HashMap<>();
    private String username;
    private String currentGroup;

    public Handler_Chat(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String input = in.readLine();
                if (input == null) {
                    logger.info("Client disconnected.");
                    break;
                }
                JsonObject requestJson = JsonHandle.input_handle(input);
                processJson(requestJson);
            }
        } catch (IOException e) {
            logger.error("Connection reset or other IOException: " + e.getMessage());
        } finally {
            leaveCurrentGroup();
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.error("Error closing socket: " + e.getMessage());
            }
        }
    }


    /**
     * @description: 处理用户发来的json请求
     * @param jsonInput 用户发来的请求
     * @return: void
     */
    private void processJson(JsonObject jsonInput) {
        // 检查是否包含消息字段
        if (jsonInput.has("message")) {
            process_message(jsonInput);
        } else {
            processJoinRequest(jsonInput);
        }
    }


    /**
     * @description: 处理用户消息
     * @param jsonInput 处理好的用户请求
     * @return: void
     */
    private void process_message(JsonObject jsonInput) {
        try {
            String jwt = jsonInput.get("Jwt").getAsString();
            username = jsonInput.get("username").getAsString();
            String group_number = String.valueOf(jsonInput.get("groupNumber"));
            String message = String.valueOf(jsonInput.get("message"));

            // JWT 验证
            Jwt.validateToken(jwt, username);

            // 加入新的聊天室
            joinGroup(group_number, username);

            // 聊天信息
            String[] data = new String[] {
                    "member", username,
                    "message", message,
                    "code", "205"
            };
            // 广播聊天消息
            broadcastMessage(data);

        } catch (JwtException.NotFound e) {
            String[] data = new String[] {"message", "JWT验证失败，无法找到", "code", "404"};
            out.println(Arrays.toString(data));
            logger.info(username + " JWT验证失败，无法找到");

        } catch (JwtException.OutOfDate e) {
            String[] data = new String[] {"message", "JWT验证失败，已过期", "code", "403"};
            out.println(Arrays.toString(data));
            logger.info(username + " JWT验证失败，已过期");

        } catch (JwtException.WrongUser e) {
            String[] data = new String[] {"message", "JWT验证失败: 错误的用户", "code", "403"};
            out.println(Arrays.toString(data));
            logger.info(username + " JWT验证失败: 错误的用户");

        } catch (JwtException.Others e) {
            String[] data = new String[] {"message", "JWT验证失败: 其他问题" + e.getMessage(), "code", "403"};
            out.println(Arrays.toString(data));
            logger.info(username + " JWT验证失败: 其他问题");
        }

    }

    /**
     * @description: 处理加入群组请求
     * @param jsonInput 用户发来的请求
     * @return: void
     */
    private void processJoinRequest(JsonObject jsonInput) {
        try {
            String jwt = jsonInput.get("Jwt").getAsString();
            username = jsonInput.get("username").getAsString();
            String groupNumber = String.valueOf(jsonInput.get("groupNumber"));

            // JWT 验证
            Jwt.validateToken(jwt, username);

            // 加入新的聊天室
            joinGroup(groupNumber, username);
            String[] data = new String[] {
                    "member", username,
                    "message", "加入了群组",
                    "code", "204"
            };

            // 广播加入聊天室消息
            broadcastMessage(data);

        } catch (JwtException.Others e) {
            sendErrorResponse("JWT验证失败: " + e.getMessage(), "405");
            logger.info("JWT验证失败: " + e.getMessage());

        } catch (Exception e) {
            sendErrorResponse("处理请求时出错: " + e.getMessage(), "403");
            logger.error("服务器内部错误: " + e);

        }
    }

    /**
     * @description: 加入聊天室逻辑
     * @param groupNumber 聊天室号
     * @param username 用户名
     * @return: void
     */
    private void joinGroup(String groupNumber, String username) {
        try {

            JDBC.group_invidy(groupNumber, username);
            JDBC.user_in_group_invidy(groupNumber, username);

            // 加入聊天室逻辑
            leaveCurrentGroup();
            currentGroup = groupNumber;
            chatRooms.computeIfAbsent(groupNumber, k -> new HashSet<>()).add(this);
        } catch (SQLException error) {
            logger.error("服务器内部错误: " + error);
        }
    }

    /**
     * @description: 离开当前的群组
     * @return: void
     */
    private void leaveCurrentGroup() {
        if (currentGroup != null && chatRooms.containsKey(currentGroup)) {
            chatRooms.get(currentGroup).remove(this);
        }
    }


    /**
     * @description: 消息广播机制
     * @param messages 需要广播的消息
     * @return: void
     */
    private void broadcastMessage(String[] messages) {
        if (currentGroup != null && chatRooms.containsKey(currentGroup)) {
            JsonObject jsonMessage = new JsonObject();

            // 假设messages数组的长度是偶数，并且每对键值都是连续的两个元素
            for (int i = 0; i < messages.length; i += 2) {
                String key = messages[i];
                String value = messages[i + 1];
                jsonMessage.addProperty(key, value);
            }

            String jsonString = jsonMessage.toString();

            for (Handler_Chat member : chatRooms.get(currentGroup)) {
                // 检查成员是否是消息的发送者
                if (!member.username.equals(this.username)) {
                    member.out.println(jsonString);
                }
            }
        }
    }

    /**
     * @description: 发送错误消息
     * @param message 需要发送的错误消息内容
     * @param code 错误代码
     * @return: void
     */
    private void sendErrorResponse(String message, String code) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("message", message);
        jsonResponse.addProperty("code", code);
        out.println(jsonResponse);
    }
}

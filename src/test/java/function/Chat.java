package function;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.JsonObject;

public class Chat {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    String username, jwt, room_number;

    public Chat(String serverAddress, int serverPort, String in_username, String in_room_number, String in_jwt) throws IOException {
        username = in_username;
        room_number = in_room_number;
        jwt = in_jwt;
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        scanner = new Scanner(System.in);
    }

    public Chat() {
    }

    public void start() {
        // 用于接收来自服务器的消息
        Thread receiveThread = new Thread(() -> {
            try {
                String serverMessage;
                // 服务器返回json格式数据
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("服务器: " + serverMessage);
                }
            } catch (IOException e) {
                System.out.println("连接已断开: " + e.getMessage());
            }
        });
        receiveThread.start();

        // 发送消息到服务器
        try {

            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("username", username);
            jsonRequest.addProperty("groupNumber", room_number);
            jsonRequest.addProperty("Jwt", jwt);

            out.println(jsonRequest);

            // 用户可以持续输入消息并发送
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("quit")) {
                    break;
                }
                JsonObject user_message = new JsonObject();
                user_message.addProperty("username", username);
                user_message.addProperty("groupNumber", room_number);
                user_message.addProperty("Jwt", jwt);
                user_message.addProperty("message", input);
                out.println(user_message);
            }
        } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
            return;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("无法关闭套接字: " + e.getMessage());
            }
        }
    }

}
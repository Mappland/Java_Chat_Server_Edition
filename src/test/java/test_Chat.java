//eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyXzEiLCJpYXQiOjE3MDMwNzg3NDYsImV4cCI6MTcwMzA4MjM0Nn0.Ojh8flfd5Jv0y-WYz_9QhyyytyNrU3DkuuaycF3zONcE4-Zi-CsM_Bu2NKsQJHrMb2UeRcl26E2fS3_AiaXvSQ

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.JsonObject;

public class test_Chat {
    // TODO 将数据库返回json数据进行解码
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Scanner scanner;

    public test_Chat(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        scanner = new Scanner(System.in);
    }

    public void start() {
        // 用于接收来自服务器的消息
        Thread receiveThread = new Thread(() -> {
            try {
                String serverMessage;
                // TODO 服务器应返回json格式数据，故此处应做修改
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
            // System.out.println("请输入用户名:");
            String username = "user_1";
            // System.out.println("请输入群组号:");
            String groupNumber = "12345";
            // System.out.println("请输入JWT:");
            String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyXzEiLCJpYXQiOjE3MDMwNzg3NDYsImV4cCI6MTcwMzA4MjM0Nn0.Ojh8flfd5Jv0y-WYz_9QhyyytyNrU3DkuuaycF3zONcE4-Zi-CsM_Bu2NKsQJHrMb2UeRcl26E2fS3_AiaXvSQ";

            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("username", username);
            jsonRequest.addProperty("groupNumber", groupNumber);
            jsonRequest.addProperty("Jwt", jwt);

            out.println(jsonRequest.toString());

                        // 用户可以持续输入消息并发送
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("quit")) {
                    break;
                }
                JsonObject user_message = new JsonObject();
                user_message.addProperty("username", username);
                user_message.addProperty("groupNumber", groupNumber);
                user_message.addProperty("Jwt", jwt);
                user_message.addProperty("message", input);
                out.println(user_message);
            }
        } catch (Exception e) {
            System.out.println("错误: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("无法关闭套接字: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            test_Chat client = new test_Chat("localhost", 12345);
            client.start();
        } catch (IOException e) {
            System.out.println("无法连接到服务器: " + e.getMessage());
        }
    }
}
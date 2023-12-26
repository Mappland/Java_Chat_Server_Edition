import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import function.Response_Client;

public class shell_ui {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int select_1 = 0;

        while (true) {
            System.out.println("请选择需要进行的项目：\n1.注册\n2.登录");
            if (scanner.hasNextInt()) {
                select_1 = scanner.nextInt();
                if (select_1 == 1 || select_1 == 2) {
                    break;
                } else {
                    System.out.println("输入不合法，请重新选择！");
                }
            } else {
                System.out.println("输入不合法，请输入数字！");
                scanner.next(); // 清除非法输入
            }
        }

        scanner.nextLine(); // 清理换行符

        if (select_1 == 1) {
            System.out.println("请输入用户名：");
            String username = scanner.nextLine();
            System.out.println("请输入密码：");
            String password = scanner.nextLine();
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            JsonObject result = Response_Client.get_response("/user_create", "POST", jsonInputString);
            if (result != null) {
                System.out.println(result);
            }
        }

        if (select_1 == 2) {
            System.out.println("请输入用户名：");
            String username = scanner.nextLine();
            System.out.println("请输入密码：");
            String password = scanner.nextLine();
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            JsonObject result = Response_Client.get_response("/user_verify", "POST", jsonInputString);
            if (result != null) {
                boolean bool_1 = Objects.equals(result.get("code").toString(), "200");
                if (bool_1){
                    String jwt = result.get("jwt").toString();
                    jwt = jwt.replace("\"", "");
                    try {
                        System.out.println("请输入要进入的房间号:");
                        String roomNumber = scanner.nextLine();
                        test_Chat client = new test_Chat("mappland.top", 12345, username, roomNumber, jwt);
                        client.start();
                    } catch (IOException e) {
                        System.out.println("无法连接到服务器: " + e.getMessage());
                    }
                }
            }
        }
    }
}
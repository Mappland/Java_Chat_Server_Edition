import java.io.IOException;

public class test_Chat_2 {
    public static void main(String[] args) {
        try {
            test_Chat client = new test_Chat("localhost", 12345, "user_2", "room_1",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyXzIiLCJpYXQiOjE3MDMxOTA0NDgsImV4cCI6MTcwMzE5NDA0OH0.zTRZ5CVw-uKx9gUklSVSTAyP42XHMM7YFZPcjhHP07Jm9dXNttnbyCGQBDZE2pkq9m2pIxrRq3AsIfr2tEn97A");
            client.start();
        } catch (IOException e) {
            System.out.println("无法连接到服务器: " + e.getMessage());
        }
    }
}

import java.io.IOException;

public class test_Chat_2 {
    public static void main(String[] args) {
        try {
            test_Chat client = new test_Chat("localhost", 12345, "user_2", "room_1",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyXzIiLCJpYXQiOjE3MDMxOTk4NTEsImV4cCI6MTcwMzIwMzQ1MX0.l3sZXRPCX35Yq3bWuKTC5-MLeHx8O2TAMz8C3hXDo_DSg-ut32EeB0t-Zfl0bWrSY69Ox_heKhr1Gur2ooxu0Q");
        client.start();
        } catch (IOException e) {
            System.out.println("无法连接到服务器: " + e.getMessage());
        }
    }
}

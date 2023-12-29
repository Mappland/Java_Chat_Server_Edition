import java.io.IOException;

public class test_Chat_2 {
    public static void main(String[] args) {
        try {
            test_Chat client = new test_Chat("mappland.top", 12345, "user_6", "room_1",
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyXzYiLCJpYXQiOjE3MDM0MDIxODgsImV4cCI6MTcwMzQwNTc4OH0.aPTfNELYuVVBiLVxgSRhUIY2SiYoCfxjevGYasEKDwW9_ad9rPi0-a1FXfyMCZX8g_mgWu6cdXtrhPskQYybaw");
        client.start();
        } catch (IOException e) {
            System.out.println("无法连接到服务器: " + e.getMessage());
        }
    }
}

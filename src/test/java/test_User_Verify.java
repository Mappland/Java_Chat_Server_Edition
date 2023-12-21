import com.google.gson.JsonObject;

import function.*;


public class test_User_Verify {

    public static void main(String[] args) {
        String jsonInputString = "{\"username\": \"user_2\", \"password\": \"examplePassword\"}";

        JsonObject result = Response_Client.get_response("/user_verify", "POST", jsonInputString);
        if (result != null) {
            System.out.println(result.toString());
        }
    }
}

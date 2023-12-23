import com.google.gson.JsonObject;

import function.Response_Client;


public class test_User_create {

    public static void main(String[] args) {
        String jsonInputString = "{\"username\": \"user_3\", \"password\": \"examplePassword\"}";

        JsonObject result = Response_Client.get_response("/user_create", "POST", jsonInputString);
        if (result != null) {
            System.out.println(result);
        }
    }
}

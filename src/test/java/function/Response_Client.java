package function;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Response_Client
{
    public static JsonObject get_response(String path, String method, String jsonInputString)
    {
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL("http://mappland.top:8080" + path);

            // 初始化连接
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream())
            {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();

            try (InputStream inputStream = (200 <= responseCode && responseCode <= 299) ?
                    connection.getInputStream() :
                    connection.getErrorStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(inputStream)))
            {

                return JsonParser.parseReader(in).getAsJsonObject();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
                connection.disconnect();
        }
        JsonObject result = new JsonObject();
        result.addProperty("message", "返回值为空");
        return result;
    }
}
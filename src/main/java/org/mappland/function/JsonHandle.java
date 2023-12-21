package org.mappland.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;



public class JsonHandle {
    /**
     * @description: 对所提交的连接进行处理
     * @param exchange 需要处理的连接
     * @return: JsonObject 返回处理后的结果
     */
    @NotNull
    public static JsonObject result_handle(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder requestBody = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            requestBody.append(inputLine);
        }
        br.close();

        // 使用 Gson 解析 JSON
        return JsonParser.parseString(requestBody.toString()).getAsJsonObject();
    }

    /**
     * @description: 处理由字符串形式给出的JSON输入
     * @param input JSON字符串
     * @return: JsonObject 返回解析后的JsonObject
     */
    public static JsonObject input_handle(String input) {
        return JsonParser.parseString(input).getAsJsonObject();
    }

    /**
     * @description: 判断所给的属性列表中的属性是否在给定的json_object中
     * @param in_json_object 给定的json_object
     * @param attributes 给定的属性列表
     * @return: boolean
     */
    public static boolean attributes_in(JsonObject in_json_object, String[] attributes) {
        for (String attribute : attributes) {
            if (!in_json_object.has(attribute)) {
                return false;
            }
        }
        return true;
    }
}

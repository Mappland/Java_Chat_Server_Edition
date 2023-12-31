package org.mappland.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    public String jwt_key;
    public int user_port;
    public int chat_port;
    public String sql_url;
    public String sql_user;
    public String sql_password;
    public Config(){
        try{
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream("config/config.yaml");
            Map<String, Object> config = yaml.load(inputStream);

            if (config.containsKey("Server")) {

                Map<String, Object> serverMap = (Map<String, Object>) config.get("Server");
                if (serverMap.containsKey("Port")) {
                    Map<String, Object> map_port = (Map<String, Object>) serverMap.get("Port");
                    if (map_port.containsKey("User_Port")) {
                        this.user_port = (int) map_port.get("User_Port");
                    }
                    if (map_port.containsKey("Chat_Port")) {
                        this.chat_port = (int) map_port.get("Chat_Port");
                    }
                }

                if (serverMap.containsKey("KEY")) {
                    Map<String, Object> map_key = (Map<String, Object>) serverMap.get("KEY");
                    if (map_key.containsKey("Jwt")) {
                        this.jwt_key = (String) map_key.get("Jwt");
                    }
                }

                if (serverMap.containsKey("SQL")) {
                    Map<String, Object> map_sql = (Map<String, Object>) serverMap.get("SQL");
                    if (map_sql.containsKey("URL")) {
                        this.sql_url = (String) map_sql.get("URL");
                    }
                    if (map_sql.containsKey("User")) {
                        this.sql_user = (String) map_sql.get("User");
                    }
                    if (map_sql.containsKey("Password")) {
                        this.sql_password = (String) map_sql.get("Password");
                    }
                }

            }
            logger.info("配置文件读取成功");
        }catch (IOException e){
            logger.error("配置文件读取错误");
        }
    }
}



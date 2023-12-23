package org.mappland.function;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mappland.Main;
import org.mappland.ProException.UserClassException;

public class JDBC {
    private static final Logger logger = LoggerFactory.getLogger(JDBC.class);
    /**
     * 数据库连接 URL。
     * URL 格式为 jdbc:mysql://主机名:端口号/数据库名。
     */
    private static final String URL = Main.config.sql_url;;
    /**
     * 数据库用户名。
     * 根据你的 MySQL 数据库设置，将其更改为相应的用户名。
     */
    private static final String USER = Main.config.sql_user;
    /**
     * 数据库密码。
     * 根据你的 MySQL 数据库设置，将其更改为相应的密码。
     */
    private static final String PASSWORD = Main.config.sql_password;

    // 静态块确保加载 JDBC 驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 根据您的 MySQL 连接器版本进行调整
        } catch (ClassNotFoundException e) {
            logger.error("无法加载 MySQL 驱动", e);
            throw new RuntimeException("无法加载 MySQL 驱动", e);
        }
    }

    /**
     * 获取数据库连接。
     *
     * @return java.sql.Connection 数据库连接对象。
     * @throws SQLException 如果在建立数据库连接时发生错误。
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    /**
     * 验证用户是否有效。
     *
     * @param username   用户名
     * @param user_passwd 用户密码
     * @return 如果用户验证成功，则返回 true；否则返回 false。
     */
    public static boolean verify_user(String username, String user_passwd) throws UserClassException.NotFound {
        if (! user_exist(username))
            throw new UserClassException.NotFound("用户不存在");

        try (Connection connection = getConnection()) {
            String sql = "SELECT user_password FROM user_table WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedPassword = resultSet.getString("user_password");
                        return Sha256.password_determine(user_passwd, storedPassword);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("数据库内部错误" + e);

        }

        return false; // 验证失败
    }


    /**
     * 添加用户
     *
     * @param username    用户名
     * @param user_passwd  用户密码
     */
    public static void create_user(String username, String user_passwd, int level) throws UserClassException {
        try (Connection connection = getConnection()) {
            if(user_exist(username))
                throw new UserClassException.UserExist("用户存在");

            // 用户名不存在，可以添加用户
            String addUserSql = "INSERT INTO user_table (username, user_password, user_level, user_create_data) VALUES (?, ?, ?, CURRENT_TIME)";
            try (PreparedStatement addUserStatement = connection.prepareStatement(addUserSql)) {
                // 为占位符设置参数值
                addUserStatement.setString(1, username);
                addUserStatement.setString(2, Sha256.password_encrypt(user_passwd));
                addUserStatement.setInt(3, level);

                // 执行添加用户的 SQL 语句
                int rowsAffected = addUserStatement.executeUpdate();

                if (rowsAffected > 0) {
                    logger.info(username + " 用户添加成功");
                } else {
                    logger.error(username + " 用户添加失败！");
                }
            }
        } catch (SQLException e) {
            logger.error("数据库内部错误" + e);
        }
    }


    /**
     * @description: 根据给定用户名判断用户是否存在
     * @param username 需要查询的用户名
     * @return: boolean
     */
    public static boolean user_exist(String username){
        String query = "SELECT COUNT(*) FROM user_table WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("数据库内部错误" + e);
        }

        return false;
    }


    /**
     * @description: 检查用户请求的群组是否存在，若不存在，则添加一条由该用户创建该群组的记录
     * @param group_number 用户请求的群组好
     * @param username 用户名
     * @return: void
     */
    public static void group_invidy(String group_number, String username) throws SQLException {
        Connection connection = getConnection();
        group_number = group_number.replace("\"", "");
        // 检查groupNumber是否存在于group_table中
        String checkGroupQuery = "SELECT COUNT(*) FROM group_table WHERE group_number = ?";
        try (PreparedStatement checkGroupStmt = connection.prepareStatement(checkGroupQuery)) {
            checkGroupStmt.setString(1, group_number);
            ResultSet rs = checkGroupStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // groupNumber不存在，向group_table中添加新记录
                String insertGroupQuery = "INSERT INTO group_table (group_number, create_date, create_user) VALUES (?, CURRENT_TIME, ?)";
                try (PreparedStatement insertGroupStmt = connection.prepareStatement(insertGroupQuery)) {
                    insertGroupStmt.setString(1, group_number);
                    insertGroupStmt.setString(2, username); // 假设username是当前用户的用户名
                    insertGroupStmt.executeUpdate();
                }
                logger.info("新建聊天组 " + group_number + " 由用户 " + username + " 创建");
            }
        }
    }



    /**
     * @description: 检查用户在群组中记录是否存在
     * @param group_number 用户请求的群组号
     * @param username 用户名
     * @return: void
     */
    public static void user_in_group_invidy(String group_number, String username) throws SQLException {
        Connection connection = getConnection();
        group_number = group_number.replace("\"", "");
        // 检查用户是否已经在user_in_group_table中
        String checkUserQuery = "SELECT COUNT(*) FROM user_in_group_table WHERE username = ? AND group_number = ?";
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setString(1, username);
            checkUserStmt.setString(2, group_number);
            ResultSet rs = checkUserStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // 用户不在user_in_group_table中，添加新记录
                String insertUserQuery = "INSERT INTO user_in_group_table (username, group_number, user_in_date) VALUES (?, ?, CURRENT_TIME)";
                try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery)) {
                    insertUserStmt.setString(1, username);
                    insertUserStmt.setString(2, group_number);
                    insertUserStmt.executeUpdate();
                }
                logger.info("添加用户：" + username + " 进入群组" + group_number);
            }

        }
    }
}

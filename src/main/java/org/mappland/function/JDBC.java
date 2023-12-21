package org.mappland.function;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.DatabaseMetaData;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mappland.ProException.UserClassException;

public class JDBC {
    private static final Logger logger = LoggerFactory.getLogger(JDBC.class);
    /**
     * 数据库连接 URL。
     * URL 格式为 jdbc:mysql://主机名:端口号/数据库名。
     * 在这个例子中，它连接到位于 mappland.top 上端口号为 3306 的 MySQL 数据库，
     * 数据库名为 steel_inventory_system。
     */
    private static final String URL = "jdbc:mysql://mappland.top:3306/chat";
    /**
     * 数据库用户名。
     * 根据你的 MySQL 数据库设置，将其更改为相应的用户名。
     */
    private static final String USER = "chat";
    /**
     * 数据库密码。
     * 根据你的 MySQL 数据库设置，将其更改为相应的密码。
     */
    private static final String PASSWORD = "123456abc";



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
            logger.error("数据库内部错误");

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
            String addUserSql = "INSERT INTO user_table (username, user_password, user_level) VALUES (?, ?, ?)";
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
            logger.error("数据库内部错误");
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
            logger.error("数据库内部错误");
        }

        return false;
    }


    public static void group_invidy(String group_number, String username) throws SQLException {
        // TODO logger in
        Connection connection = getConnection();
        // 检查groupNumber是否存在于group_table中
        String checkGroupQuery = "SELECT COUNT(*) FROM group_table WHERE group_number = ?";
        try (PreparedStatement checkGroupStmt = connection.prepareStatement(checkGroupQuery)) {
            checkGroupStmt.setString(1, group_number);
            ResultSet rs = checkGroupStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // groupNumber不存在，向group_table中添加新记录
                String insertGroupQuery = "INSERT INTO group_table (group_number, create_date, create_user) VALUES (?, CURRENT_DATE, ?)";
                try (PreparedStatement insertGroupStmt = connection.prepareStatement(insertGroupQuery)) {
                    insertGroupStmt.setString(1, group_number);
                    insertGroupStmt.setString(2, username); // 假设username是当前用户的用户名
                    insertGroupStmt.executeUpdate();
                }
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
        // 检查用户是否已经在user_in_group_table中
        String checkUserQuery = "SELECT COUNT(*) FROM user_in_group_table WHERE username = ? AND group_number = ?";
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setString(1, username);
            checkUserStmt.setString(2, group_number);
            ResultSet rs = checkUserStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // 用户不在user_in_group_table中，添加新记录
                String insertUserQuery = "INSERT INTO user_in_group_table (username, group_number, user_in_date) VALUES (?, ?, CURRENT_DATE)";
                try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery)) {
                    insertUserStmt.setString(1, username);
                    insertUserStmt.setString(2, group_number);
                    insertUserStmt.executeUpdate();
                }
            }
            logger.info("添加用户：" + username + " 进入群组" + group_number);
        }
    }



    /**
     * 获取指定表的所有列名。
     *
     * @param connection 数据库连接对象。
     * @param tableName  要获取列名的表名。
     * @return 包含表中所有列名的列表。
     * @throws SQLException 如果在获取列名过程中发生错误。
     */
    private static List<String> getColumnNames(Connection connection, String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, null, tableName, null)) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }
        }
        return columnNames;
    }


    /**
     * 获取指定表的主键列名列表。
     *
     * @param connection 数据库连接对象。
     * @param tableName  表名。
     * @return 包含主键列名的列表。
     * @throws SQLException 如果在获取主键信息过程中发生错误。
     */
    private static List<String> getPrimaryKeyColumns(Connection connection, String tableName) throws SQLException {
        List<String> primaryKeyColumns = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet resultSet = metaData.getPrimaryKeys(null, null, tableName)) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                primaryKeyColumns.add(columnName);
            }
        }

        return primaryKeyColumns;
    }


    /**
     * 读取指定表中的所有数据。
     *
     * @param tableName 要读取数据的表名。
     * @return 包含所有数据的 StringBuilder 对象，每行数据按列名:值的格式排列，以制表符分隔。
     * @throws SQLException 如果在数据库访问过程中发生错误。
     */
    public static StringBuilder read_table_all_data(String tableName) {
        StringBuilder result = new StringBuilder();
        try (Connection connection = getConnection()) {
            // 动态获取表中所有列名
            List<String> columnNames = getColumnNames(connection, tableName);

            // 添加表的属性名到结果中
            for (String columnName : columnNames) {
                result.append(String.format("%-20s", columnName)).append("\t");
            }
            result.append("\n");

            // 构建 SQL 语句
            String sql = "SELECT * FROM " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    for (String columnName : columnNames) {
                        // 根据列名获取对应的值
                        String columnValue = resultSet.getString(columnName);
                        result.append(String.format("%-20s", columnValue)).append("\t");
                    }
                    result.append("\n");
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 调用存储过程。
     *
     * @param storedName 存储过程的名称。
     * @param valueArgs  存储过程的参数，支持不定数量的参数。
     *                   参数按照在存储过程中的顺序传递。
     *                   可以传递基本数据类型和对象类型。
     * @throws SQLException 如果在数据库访问过程中发生错误。
     */
    public static void callStoredProcedure(String storedName, Object... valueArgs) {
        try (Connection connection = getConnection()) {
            // 构建存储过程调用的 SQL 语句
            StringBuilder sqlBuilder = new StringBuilder("{call ").append(storedName).append("(");

            // 在 SQL 语句中添加参数占位符
            for (int i = 0; i < valueArgs.length; i++) {
                sqlBuilder.append("?");
                if (i < valueArgs.length - 1) {
                    sqlBuilder.append(", ");
                }
            }

            sqlBuilder.append(")}");

            try (CallableStatement statement = connection.prepareCall(sqlBuilder.toString())) {
                // 设置存储过程参数
                for (int i = 0; i < valueArgs.length; i++) {
                    statement.setObject(i + 1, valueArgs[i]);
                }

                statement.execute();
                System.out.println("存储过程调用成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过指定的表名和主键值删除数据行。
     *
     * @param tableName 表名。
     * @param keyValues 主键值。
     * @throws SQLException 如果在数据库访问过程中发生错误。
     */
    public static void deleteData(String tableName, String keyValues) {
        try (Connection connection = getConnection()) {
            // 获取表的主键列名
            List<String> primaryKeyColumns = getPrimaryKeyColumns(connection, tableName);

            // 构建 SQL 语句，动态生成主键条件
            StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
            for (int i = 0; i < primaryKeyColumns.size(); i++) {
                sqlBuilder.append(primaryKeyColumns.get(i)).append(" = ?");
                if (i < primaryKeyColumns.size() - 1) {
                    sqlBuilder.append(" AND ");
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())) {
                // 设置主键值
                String[] values = keyValues.split("AND");  // 假设主键值之间使用逗号分隔
                for (int i = 0; i < values.length; i++) {
                    statement.setString(i + 1, values[i]);
                }

                statement.executeUpdate();
                System.out.println("数据删除成功！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    /**
     * 从指定的表中选择数据并返回结果，如果key_value中包含'%'则进行模糊匹配
     *
     * @param tableName 表名
     * @param key       要匹配的键
     * @param key_value 键的值
     * @return 包含选择数据结果的 {@link StringBuilder}
     */
    public static StringBuilder read_table_select_data(String tableName, String key, String key_value) {
        StringBuilder result = new StringBuilder();
        try (Connection connection = getConnection()) {
            // 动态获取表中所有列名
            List<String> columnNames = getColumnNames(connection, tableName);

            // 添加表的属性名到结果中
            for (String columnName : columnNames) {
                result.append(String.format("%-20s", columnName)).append("\t");
            }
            result.append("\n");

            if (key_value.contains("%"))
            {
                String sql = "SELECT * FROM " + tableName + " WHERE " + key + " LIKE ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, key_value);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            for (String columnName : columnNames) {
                                // 根据列名获取对应的值
                                String columnValue = resultSet.getString(columnName);
                                result.append(String.format("%-20s", columnValue)).append("\t");
                            }
                            result.append("\n");
                        }
                    }
                }
            }
            else
            // 构建 SQL 语句
            {
                String sql = "SELECT * FROM " + tableName + " WHERE " + key + " = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, key_value);
                    System.out.println(statement);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            for (String columnName : columnNames) {
                                // 根据列名获取对应的值
                                String columnValue = resultSet.getString(columnName);
                                result.append(String.format("%-20s", columnValue)).append("\t");
                            }
                            result.append("\n");
                        }
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



}

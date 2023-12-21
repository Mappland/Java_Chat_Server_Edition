package org.mappland.chat_class;

import org.mappland.ProException.JwtException;
import org.mappland.ProException.UserClassException;
import org.mappland.function.*;

import java.sql.SQLException;

public class User{
    public StringBuilder user_name;
    public StringBuilder user_password;
    public StringBuilder user_jwt;


    public User(String in_username, String in_user_password)
    {
        user_name = new StringBuilder(in_username);
        user_password = new StringBuilder(in_user_password);
    }


    public static void create_user(String in_username, String in_user_passwd) throws UserClassException
    {
        if (password_check(in_user_passwd))
            throw new UserClassException.PasswordIllegal("密码不合法");
        try {
            JDBC.create_user(in_username, in_user_passwd, 0);
        } catch (UserClassException.UserExist exist) {
            throw new UserClassException.UserExist("用户已经存在");
        }

    }

    static boolean password_check(String in_passwd)
    {
        // 定义一个正则表达式，排除 %, $, *, @
        String regex = "^[^%$*@]+$";

        // 使用 String 的 matches 方法来检查字符串是否符合正则表达式
        // 如果不匹配，说明存在非法字符
        return !in_passwd.matches(regex);
    }


    // TODO 用户密码错误应抛出 UserClassException.PasswordError
    public boolean verify_user() throws JwtException, UserClassException.NotFound, SQLException {
        if (!JDBC.verify_user(this.user_name.toString(), this.user_password.toString()))
            return false;

        this.user_jwt = new StringBuilder(Jwt.Jwt_generate(this.user_name.toString()));
        return true;
    }

}
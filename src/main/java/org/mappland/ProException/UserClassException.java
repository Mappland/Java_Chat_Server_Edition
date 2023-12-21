package org.mappland.ProException;

public class UserClassException extends Exception {

    // 构造器
    UserClassException(String message) {
        super(message);
    }

    // NotFound 异常
    public static class NotFound extends UserClassException {
        public NotFound(String message) {
            super(message);
        }
    }


    // PasswordIllegal 异常
    public static class PasswordIllegal extends UserClassException {
        public PasswordIllegal(String message) {
            super(message);
        }
    }

    public static class UserExist extends UserClassException {
        public UserExist(String message) {
            super(message);
        }
    }
    // 其他可能的异常类型...
}

package org.mappland.ProException;

public class JwtException extends Exception {
    public JwtException(String message) {
        super(message);
    }

    public static class NotFound extends JwtException {
        public NotFound(String message) {
            super(message);
        }
    }

    public static class OutOfDate extends JwtException {
        public OutOfDate(String message) {
            super(message);
        }
    }

    public static class WrongUser extends JwtException {
        public WrongUser(String message) {
            super(message);
        }
    }

    public static class Others extends JwtException {
        public Others(String message) {
            super(message);
        }
    }

    public static class CreateError extends JwtException {
        public CreateError(String message) {
            super(message);
        }
    }
}

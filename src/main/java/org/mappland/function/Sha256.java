package org.mappland.function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

    public static String password_encrypt(String password_in) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password_in.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return new String(hexString);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static boolean password_determine(String plainPassword, String encryptedPassword) {
        String encryptedInputPassword = password_encrypt(plainPassword);
        return encryptedInputPassword.equals(encryptedPassword);
    }
}

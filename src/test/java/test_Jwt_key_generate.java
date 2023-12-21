import java.security.SecureRandom;
import java.util.Base64;

public class test_Jwt_key_generate {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[64]; // 生成一个64字节（512位）的秘钥
        random.nextBytes(keyBytes);
        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Secret Key: " + secretKey);
    }
}
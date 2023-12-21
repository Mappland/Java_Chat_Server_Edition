import org.mappland.ProException.JwtException;
import org.mappland.function.Jwt;

public class test_Jwt_Create {
    public static void main(String[] args) {
        // 测试生成JWT
        try{
            String token = Jwt.Jwt_generate("username"); // 1小时的有效期
            System.out.println("Generated JWT: " + token);
        }catch (JwtException.CreateError error) {
            System.out.println("创建错误");
        }
    }
}

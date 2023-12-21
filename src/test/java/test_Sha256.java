import org.mappland.function.Sha256;
public class test_Sha256 {
    public static void main(String[] args)
    {
        String password = "example_password";
        String password_sha = Sha256.password_encrypt(password);
        System.out.println(Sha256.password_determine(password, password_sha));
    }

}

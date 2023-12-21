package org.mappland.function;

import org.mappland.Handler.Handler_Verify_User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureException;
import java.util.Date;

import org.mappland.ProException.JwtException;


public class Jwt {

    private static final Logger logger = LoggerFactory.getLogger(Jwt.class);
    private static final String SECRET_KEY = "2pSlaRClnVCFKK2t0TqWBAKfMGMvFYS34NkaxKZDxiZmCyrBCgpXfGFYoM" +
            "fgHho/GeoUkwY7waNaQkpgEFp/Ew=="; // 替换为你的秘钥

    public static String Jwt_generate(String username) throws JwtException.CreateError {
        try {
            Date now = new Date();
            // 设置过期时间为3600000ms（1小时）
            Date expiryDate = new Date(now.getTime() + 3600000);

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact();
        } catch (Exception e) {
            throw new JwtException.CreateError("JWT token generation failed" + e);
        }
    }



    public static void validateToken(String token, String username)
            throws JwtException.NotFound, JwtException.OutOfDate, JwtException.WrongUser, JwtException.Others {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();
            Date now = new Date();

            // 检查过期
            if (claims.getExpiration().before(now)) {
                logger.error("Jwt已过期");
                throw new JwtException.OutOfDate("Jwt已过期");
            }

            // 检查用户名
            if (!claims.getSubject().equals(username)) {
                logger.error("Jwt与用户名不符");
                throw new JwtException.WrongUser("Jwt与用户名不符");
            }
        } catch (SignatureException e) {
            logger.error("Jwt签名不符");
            throw new JwtException.NotFound("Jwt签名不符");

        } catch (Exception e) {
            // 其他异常情况
            logger.error(e.toString());
            throw new JwtException.Others("Other JWT validation error: " + e.getMessage());
        }
    }
}

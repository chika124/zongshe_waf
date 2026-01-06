package org.example.waf.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 至少 32 字节，保证 HS256 不报错（建议放到 application.properties）
    private static final String SECRET = "your_jwt_secret_key_which_is_long_enough_123456";

    // 生成符合要求的 Key
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成 JWT Token
     */
    public static String generateToken(String userId, String username) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600 * 1000; // 1小时有效

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(expMillis))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验 Token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true; // 验证成功
        } catch (JwtException e) {
            return false; // 签名无效、过期、格式错误都会抛异常
        }
    }
}

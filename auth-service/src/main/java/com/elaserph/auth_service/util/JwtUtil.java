package com.elaserph.auth_service.util;

import com.elaserph.auth_service.entity.MyUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public final class JwtUtil {

    private static final String SECRET_KEY = "C4F3994495E89617B5484D5136AA515F19D094229DE38C068C802CB1E78288216C02F06CA559BFDAC9FF63FB083B108B7C572EF3344BA423FA3BCEC0C861D99C";

    //private constructor
    private JwtUtil(){
    }

    public static String generateJwtToken(MyUser user, long validity){
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(validity)))
                .signWith(generateKey())
                .compact();
    }

    private static SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public static Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}

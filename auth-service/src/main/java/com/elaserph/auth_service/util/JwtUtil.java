package com.elaserph.auth_service.util;

import com.elaserph.auth_service.entity.MyUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class JwtUtil {

    private static final String SECRET_KEY = "C4F3994495E89617B5484D5136AA515F19D094229DE38C068C802CB1E78288216C02F06CA559BFDAC9FF63FB083B108B7C572EF3344BA423FA3BCEC0C861D99C";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);

    //private constructor
    private JwtUtil(){
    }

    public static String generateJwtToken(MyUser user){
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }

    private static Key generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}

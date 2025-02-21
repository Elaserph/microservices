package com.elaserph.auth_service.util;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import java.security.Key;

class JwtSecretMakerTest {

    @Test
    void generateSecretKey(){
        Key key = Jwts.SIG.HS512.key().build();
        String encodedKey = DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.println("EncodedKey: " +encodedKey);
    }
}

package com.elaserph.auth_service.util;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

class JwtSecretMakerTest {

    @Test
    void generateSecretKey(){
        SecretKey key = Jwts.SIG.HS512.key().build();
        String encodedKey = DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.println("EncodedKey: " +encodedKey);
    }
}

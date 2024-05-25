package com.fatec.donation.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Component
public class SecretKeyGenerator {
    private SecretKey key;

    public SecretKey getKey() {
        if (key == null) {
            key = generateSecretKey();
        }
        return key;
    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(SignatureAlgorithm.HS256.getJcaName());
            keyGenerator.init(256); // Set key size to 256 bits
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate secret key", e);
        }
    }

}

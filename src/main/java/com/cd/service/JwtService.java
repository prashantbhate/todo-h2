package com.cd.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
    @Autowired
    private KeyPair keyPair;

    public String generateJwt(String subject, @NonNull Map<String, Object> claims) {
        PrivateKey privateKey = keyPair.getPrivate();
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(privateKey)
                .claims(claims);

        return jwtBuilder.compact();
    }

    public Claims validateJwt(String jwt) {
        PublicKey publicKey = keyPair.getPublic();
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(publicKey)
                .build();
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }
}

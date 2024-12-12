package com.cd.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtStore {

    private final Cache<String, String> jtiCache;

    public JwtStore() {
        jtiCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES) // Evict after 1 hour
                .build();
    }


    public void saveJti(String username, String jti) {
        jtiCache.put(jti, username);
    }

    public boolean isJtiValid(String username, String jti) {
        return username.equals(jtiCache.getIfPresent(jti));
    }

    public void invalidateJti(String jti) {
        jtiCache.invalidate(jti);
    }
}
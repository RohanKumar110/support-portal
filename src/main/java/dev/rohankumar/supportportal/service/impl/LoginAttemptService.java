package dev.rohankumar.supportportal.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int EXPIRES_MINUTES = 15;
    private static final int MAX_NUMBER_OF_ATTEMPTS = 3;
    private static final int ATTEMPT_INCREMENT = 1;
    private static final long MAX_ENTRIES = 100L;
    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        this.loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRES_MINUTES, TimeUnit.MINUTES)
                .maximumSize(MAX_ENTRIES)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username)  {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAX_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}

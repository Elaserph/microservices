package com.elaserph.auth_service.service;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final long ACCESS_TOKEN_VALIDITY = TimeUnit.MINUTES.toMillis(30);
    private static final long REFRESH_TOKEN_VALIDITY = TimeUnit.DAYS.toMillis(7);
    private final Map<String, String> refreshTokenMap = new HashMap<>();

    public String createAccessToken(MyUser user){
        return JwtUtil.generateJwtToken(user, ACCESS_TOKEN_VALIDITY);
    }

    public void createRefreshToken(MyUser user){
        var refreshToken = JwtUtil.generateJwtToken(user, REFRESH_TOKEN_VALIDITY);
        refreshTokenMap.put(user.getUsername(), refreshToken);
    }
}

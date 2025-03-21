package com.elaserph.auth_service.service;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.repository.MyUserRepository;
import com.elaserph.auth_service.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private final MyUserRepository userRepository;
    private static final long ACCESS_TOKEN_VALIDITY = TimeUnit.MINUTES.toMillis(1);
    private static final long REFRESH_TOKEN_VALIDITY = TimeUnit.DAYS.toMillis(7);
    private final Map<String, String> refreshTokenMap = new HashMap<>();

    public JwtService(MyUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createAccessToken(MyUser user) {
        return JwtUtil.generateJwtToken(user, ACCESS_TOKEN_VALIDITY);
    }

    public void createRefreshToken(MyUser user) {
        var refreshToken = JwtUtil.generateJwtToken(user, REFRESH_TOKEN_VALIDITY);
        refreshTokenMap.put(user.getUsername(), refreshToken);
    }

    public Boolean validateToken(String token) {
        var user = getMyUser(token);
        return (user.isPresent() && !JwtUtil.isTokenExpired(token));
    }

    public String refreshToken(String token) {
        var user = getMyUser(token);
        if (user.isPresent() && refreshTokenMap.containsKey(user.get().getUsername())) {
            var refreshToken = refreshTokenMap.get(user.get().getUsername());
            Boolean isRefreshTokenExpired = JwtUtil.isTokenExpired(refreshToken);
            if(Boolean.FALSE.equals(isRefreshTokenExpired)){
                return JwtUtil.generateJwtToken(user.get(), ACCESS_TOKEN_VALIDITY);
            } else
                return "Please Login Again!";
        } else
            return "Invalid token";
    }

    public Optional<MyUser> getMyUser(String token) {
        String extractedUsername = JwtUtil.extractUsername(token);
        return userRepository.getByUsername(extractedUsername);
    }

    public String logout(String token) {
        var user = getMyUser(token);
        if (user.isPresent() && refreshTokenMap.containsKey(user.get().getUsername())) {
            refreshTokenMap.remove(user.get().getUsername());
            return "Logout Successful!!!";
        }
        return "User already logged out or not found!";
    }
}

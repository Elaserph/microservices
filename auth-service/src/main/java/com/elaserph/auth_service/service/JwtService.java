package com.elaserph.auth_service.service;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String createAccessToken(MyUser user){
        return JwtUtil.generateJwtToken(user);
    }
}

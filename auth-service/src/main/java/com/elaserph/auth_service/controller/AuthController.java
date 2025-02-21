package com.elaserph.auth_service.controller;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.service.JwtService;
import com.elaserph.auth_service.service.MyUserDetailsService;
import com.elaserph.auth_service.service.MyUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MyUserDetailsService myUserDetailsService;
    private final MyUserService myUserService;
    private final JwtService jwtService;

    public AuthController(MyUserDetailsService myUserDetailsService,
                          MyUserService myUserService, JwtService jwtService){
        this.myUserDetailsService = myUserDetailsService;
        this.myUserService = myUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody MyUser user){
        var savedUser = myUserService.saveMyUser(user);
        return jwtService.createAccessToken(savedUser);
    }
}

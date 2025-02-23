package com.elaserph.auth_service.controller;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.service.JwtService;
import com.elaserph.auth_service.service.MyUserDetailsService;
import com.elaserph.auth_service.service.MyUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final MyUserDetailsService myUserDetailsService;
    private final MyUserService myUserService;
    private final JwtService jwtService;


    public AuthController(MyUserDetailsService myUserDetailsService,
                          MyUserService myUserService, JwtService jwtService) {
        this.myUserDetailsService = myUserDetailsService;
        this.myUserService = myUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody MyUser user) {
        System.out.println("in register api");
        if(myUserService.getByUser(user).isEmpty()) {
            var savedUser = myUserService.saveMyUser(user);
            jwtService.createRefreshToken(savedUser);
            return new ResponseEntity<>(jwtService.createAccessToken(user), HttpStatus.OK);
        } else
            return new ResponseEntity<>("User is already registered", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody MyUser user) {
        System.out.println("in authentication api");
        try {
            Authentication authentication = myUserService.authenticateUser(user);
            if (authentication.isAuthenticated()) {
                jwtService.createRefreshToken(user);
                return new ResponseEntity<>(jwtService.createAccessToken(user), HttpStatus.OK);
            } else
                throw new UsernameNotFoundException("Invalid access");
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Authentication failed for user: " + user.getUsername(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        Boolean valid = jwtService.validateToken(token);
        System.out.println("in token validation api, validity: "+valid);
        return new ResponseEntity<>(valid, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestParam("token") String token) {
        String refreshToken = jwtService.refreshToken(token);
        System.out.println("in token refresh api, refreshed: "+refreshToken);
        return new ResponseEntity<>(jwtService.refreshToken(refreshToken), HttpStatus.OK);
    }
}

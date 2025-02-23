package com.elaserph.auth_service.controller;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.service.JwtService;
import com.elaserph.auth_service.service.MyUserDetailsService;
import com.elaserph.auth_service.service.MyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
        log.info("Register api");
        if(myUserService.getByUser(user).isEmpty()) {
            var savedUser = myUserService.saveMyUser(user);
            jwtService.createRefreshToken(savedUser);
            return new ResponseEntity<>(jwtService.createAccessToken(user), HttpStatus.OK);
        } else
            return new ResponseEntity<>("User is already registered", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticateUser(@RequestBody MyUser user) {
        log.info("Authentication api");
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
        log.info("Token validation api");
        return new ResponseEntity<>(jwtService.validateToken(token), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestParam("token") String token) {
        log.info("Token refresh api");
        return new ResponseEntity<>(jwtService.refreshToken(token), HttpStatus.OK);
    }

    @RequestMapping("/user")
    public Principal user(Principal user){
        return user;
    }
}

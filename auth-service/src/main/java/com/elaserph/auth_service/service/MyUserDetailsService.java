package com.elaserph.auth_service.service;

import com.elaserph.auth_service.entity.MyUser;
import com.elaserph.auth_service.repository.MyUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final MyUserRepository userRepository;

    public MyUserDetailsService(MyUserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(getRoles(userObj.getRole()))
                    .build();
        } else
            throw new UsernameNotFoundException(username);
    }

    private String[] getRoles(String role) {
        if(role == null)
            return new String[]{"USER"};
        return role.split(",");
    }
}

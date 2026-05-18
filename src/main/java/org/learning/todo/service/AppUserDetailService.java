package org.learning.todo.service;

import org.learning.todo.controller.request.UserRegistrationRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class AppUserDetailService implements UserDetailsService {
    private final ConcurrentHashMap<String, UserDetails> users;
    private final PasswordEncoder passwordEncoder;

    public AppUserDetailService(ConcurrentHashMap<String, UserDetails> users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = this.users.get(username);
        if(userDetails == null) throw new UsernameNotFoundException("Username {} not found".formatted(username));
        return userDetails;
    }

    public void registerUser(UserRegistrationRequest request) {
        UserDetails user = User.builder()
                .passwordEncoder(this.passwordEncoder::encode)
                .username(request.username())
                .password(request.password())
                .roles("USER")
                .build();

        this.users.put(user.getUsername(), user);
    }
}

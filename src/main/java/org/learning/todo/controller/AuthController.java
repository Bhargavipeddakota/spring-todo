package org.learning.todo.controller;

import org.learning.todo.controller.request.UserRegistrationRequest;
import org.learning.todo.controller.view.LoggedInUser;
import org.learning.todo.service.AppUserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserDetailService appUserDetailService;

    public AuthController(AppUserDetailService appUserDetailService) {
        this.appUserDetailService = appUserDetailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        this.appUserDetailService.registerUser(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/whoami")
    public LoggedInUser whoAmI(Authentication authentication) {
        return new LoggedInUser(authentication.getName());
    }
}

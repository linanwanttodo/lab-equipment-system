package com.barezen.controller;

import com.barezen.dto.AuthResponse;
import com.barezen.dto.LoginRequest;
import com.barezen.dto.RegisterRequest;
import com.barezen.dto.UserResponse;
import com.barezen.exception.ApiException;
import com.barezen.security.AppUser;
import com.barezen.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return authService.me(currentUserId(authentication));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUser appUser) {
            return appUser.getId();
        }
        throw new ApiException(401, "未登录");
    }
}

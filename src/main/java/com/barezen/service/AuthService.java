package com.barezen.service;

import com.barezen.dto.AuthResponse;
import com.barezen.dto.LoginRequest;
import com.barezen.dto.RegisterRequest;
import com.barezen.dto.UserResponse;
import com.barezen.entity.Role;
import com.barezen.entity.User;
import com.barezen.exception.ApiException;
import com.barezen.repository.UserRepository;
import com.barezen.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** 注册：新用户默认为学生角色。 */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new ApiException(409, "用户名已存在");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setName(req.getName());
        u.setContact(req.getContact());
        u.setRole(Role.STUDENT);
        u = userRepository.save(u);
        return toAuthResponse(u, jwtService.generateToken(u.getId(), u.getUsername(), u.getRole()));
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByUsername(req.getUsername()).orElse(null);
        if (u == null || !passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new ApiException(401, "用户名或密码错误");
        }
        return toAuthResponse(u, jwtService.generateToken(u.getId(), u.getUsername(), u.getRole()));
    }

    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new ApiException(404, "用户不存在"));
        return new UserResponse(u.getId(), u.getUsername(), u.getName(), u.getContact(), u.getRole(), u.getCreatedAt());
    }

    private AuthResponse toAuthResponse(User u, String token) {
        return new AuthResponse(token, u.getId(), u.getUsername(), u.getName(), u.getRole());
    }
}

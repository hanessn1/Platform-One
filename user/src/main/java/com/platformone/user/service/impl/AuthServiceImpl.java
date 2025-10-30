package com.platformone.user.service.impl;

import com.platformone.user.dto.LoginRequestDTO;
import com.platformone.user.dto.LoginResponseDTO;
import com.platformone.user.dto.RegisterRequestDTO;
import com.platformone.user.entity.User;
import com.platformone.user.jwt.JwtUtils;
import com.platformone.user.service.AuthService;
import com.platformone.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public User registerUser(RegisterRequestDTO registerRequest) {
        String hashed = passwordEncoder.encode(registerRequest.getPassword());
        User user = new User(registerRequest.getName(), registerRequest.getEmail(), hashed);
        return userService.createUser(user);
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());
        if (user == null) return null;
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getHashedPassword())) {
            return null;
        }
        String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getRole());
        return new LoginResponseDTO(loginRequest.getEmail(), jwtToken, user.getRole());
    }
}
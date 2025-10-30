package com.platformone.user.controller;

import com.platformone.user.dto.LoginRequestDTO;
import com.platformone.user.dto.LoginResponseDTO;
import com.platformone.user.dto.RegisterRequestDTO;
import com.platformone.user.entity.User;
import com.platformone.user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        User savedUser = authService.registerUser(registerRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getUserId());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.authenticateUser(loginRequest);
        if (response == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
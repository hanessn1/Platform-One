package com.platformone.user.service;

import com.platformone.user.dto.LoginRequestDTO;
import com.platformone.user.dto.LoginResponseDTO;
import com.platformone.user.dto.RegisterRequestDTO;
import com.platformone.user.entity.User;

public interface AuthService {
    User registerUser(RegisterRequestDTO registerRequest);

    LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest);
}
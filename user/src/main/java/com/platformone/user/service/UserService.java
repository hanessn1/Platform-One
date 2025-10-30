package com.platformone.user.service;

import com.platformone.user.dto.UserProfileDTO;
import com.platformone.user.entity.User;

import java.util.Optional;

public interface UserService {

    User getUserById(long userId);

    Optional<User> updateUser(long userId, User updatedUser);

    boolean deleteUser(long userId);

    User createUser(User newUser);

    UserProfileDTO getUserProfile(String email);

    User getUserByEmail(String email);
}
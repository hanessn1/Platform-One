package com.platformone.user.service;

import com.platformone.user.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

    User getUserById(long userId);

    Optional<User> updateUser(long userId, User updatedUser);

    boolean deleteUser(long userId);

    User createUser(User newUser);
}

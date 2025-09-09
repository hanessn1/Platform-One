package com.platformone.user.service.impl;

import com.platformone.user.entity.User;
import com.platformone.user.repository.UserRepository;
import com.platformone.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public Optional<User> updateUser(long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setHashedPassword(updatedUser.getHashedPassword());
            return userRepository.save(user);
        });
    }

    @Override
    public boolean deleteUser(long userId) {
        User user=getUserById(userId);
        if(user==null) return false;
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public User createUser(User newUser) {
        return userRepository.save(newUser);
    }
}

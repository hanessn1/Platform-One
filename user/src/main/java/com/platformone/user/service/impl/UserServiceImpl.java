package com.platformone.user.service.impl;

import com.platformone.user.clients.PaymentClient;
import com.platformone.user.dto.WalletCreateRequestDTO;
import com.platformone.user.entity.User;
import com.platformone.user.exception.DuplicateEmailException;
import com.platformone.user.external.Wallet;
import com.platformone.user.repository.UserRepository;
import com.platformone.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    public UserServiceImpl(UserRepository userRepository, PaymentClient paymentClient1) {
        this.userRepository = userRepository;
        this.paymentClient = paymentClient1;
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User createUser(User newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new DuplicateEmailException("A user with this email already exists");
        }

        User savedUser = userRepository.save(newUser);
        try {
            Wallet wallet = paymentClient.initializeWallet(
                    new WalletCreateRequestDTO(savedUser.getUserId(), 1000.0)
            );
            if (wallet != null) {
                log.info("Wallet created successfully for user {}, balance = {}",
                        wallet.getUserId(), wallet.getBalance());
            }
        } catch (Exception e) {
            log.error("Failed to create wallet for user {}", savedUser.getUserId(), e);
        }

        return savedUser;
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
        User user = getUserById(userId);
        if (user == null) return false;
        userRepository.deleteById(userId);
        return true;
    }
}

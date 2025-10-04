package com.platformone.user.repository;

import com.platformone.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = new User("Alice", "alice@example.com", "hashedpassword");
        userRepository.save(user);
    }

    @Test
    void testFindById() {
        Optional<User> found = userRepository.findById(user.getUserId());
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void testSaveAnotherUser() {
        User newUser = new User("Bob", "bob@example.com", "pass");
        User saved = userRepository.save(newUser);
        assertNotNull(saved.getUserId());
        assertEquals("Bob", saved.getName());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user);
        assertFalse(userRepository.findById(user.getUserId()).isPresent());
    }
}
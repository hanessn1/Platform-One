package com.platformone.user.service.impl;

import com.platformone.user.entity.User;
import com.platformone.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("Alice", "alice@example.com", "hashedpassword");
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.getUserById(1L);
        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        User found = userService.getUserById(1L);
        assertNull(found);
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User created = userService.createUser(user);
        assertNotNull(created);
        assertEquals("Alice", created.getName());
        assertEquals("alice@example.com", created.getEmail());
    }

    @Test
    void testUpdateUser_UserExists() {
        User updatedUser = new User("Alice Updated", "alice@example.com", "newpassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        Optional<User> result = userService.updateUser(1L, updatedUser);
        assertTrue(result.isPresent());
        assertEquals("Alice Updated", result.get().getName());
        assertEquals("newpassword", result.get().getHashedPassword());
    }

    @Test
    void testUpdateUser_UserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<User> result = userService.updateUser(1L, user);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);
        boolean deleted = userService.deleteUser(1L);
        assertTrue(deleted);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        boolean deleted = userService.deleteUser(1L);
        assertFalse(deleted);
        verify(userRepository, never()).deleteById(1L);
    }
}
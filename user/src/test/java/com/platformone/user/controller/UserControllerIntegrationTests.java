package com.platformone.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.user.clients.PaymentClient;
import com.platformone.user.config.SecurityConfig;
import com.platformone.user.dto.UserProfileDTO;
import com.platformone.user.entity.User;
import com.platformone.user.exception.DuplicateEmailException;
import com.platformone.user.jwt.CustomAccessDeniedHandler;
import com.platformone.user.jwt.JwtAuthenticationEntryPoint;
import com.platformone.user.jwt.JwtUtils;
import com.platformone.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PaymentClient paymentClient;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setup() {
        user = new User("Alice", "alice@example.com", "hashedpassword");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/user/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(null);

        mockMvc.perform(get("/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_Success() throws Exception {
        User newUser = new User("Bob", "bob@example.com", "pass");
        User savedUser = new User("Bob", "bob@example.com", "pass");

        when(userService.createUser(Mockito.any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_DuplicateEmail() throws Exception {
        User duplicateUser = new User("AliceDuplicate", "alice@example.com", "pass");

        when(userService.createUser(Mockito.any(User.class)))
                .thenThrow(new DuplicateEmailException("A user with this email already exists"));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with this email already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_InvalidEmail() throws Exception {
        User invalidUser = new User("Charlie", "invalid-email", "pass");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_Success() throws Exception {
        User existingUser = new User("Alice", "alice@example.com", "hashedpassword");
        User updatedUser = new User("Alice Updated", "alice@example.com", "hashedpassword");

        when(userService.updateUser(eq(1L), Mockito.any(User.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_NotFound() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/user/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void testGetUserProfile_Success() throws Exception {
        UserDetails mockDetailsForService = org.springframework.security.core.userdetails.User
                .withUsername("alice@example.com")
                .password("pass")
                .roles("USER")
                .build();

        UserProfileDTO profile = new UserProfileDTO(1L,"Alice", "alice@example.com", Instant.now()); // Make sure name matches if needed

        when(userDetailsService.loadUserByUsername("alice@example.com")).thenReturn(mockDetailsForService);
        when(userService.getUserProfile("alice@example.com")).thenReturn(profile);

        mockMvc.perform(get("/user/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }
}
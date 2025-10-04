package com.platformone.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.user.entity.User;
import com.platformone.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        user = new User("Alice", "alice@example.com", "hashedpassword");
        userRepository.save(user);
    }

    @Test
    void testGetUserById_Success() throws Exception {
        mockMvc.perform(get("/user/" + user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get("/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        User newUser = new User("Bob", "bob@example.com", "pass");
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User savedUser = userRepository.save(user);
        User updatePayload = new User();
        updatePayload.setName("Alice Updated");
        updatePayload.setEmail(savedUser.getEmail());
        updatePayload.setHashedPassword("newpassword");

        mockMvc.perform(put("/user/" + savedUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.hashedPassword").value("newpassword"))
                .andExpect(jsonPath("$.userId").value(savedUser.getUserId()))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        mockMvc.perform(delete("/user/" + user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }
}

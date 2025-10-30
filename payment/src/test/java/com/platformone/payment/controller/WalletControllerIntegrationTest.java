package com.platformone.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.payment.config.SecurityConfig;
import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.exception.DuplicateWalletForUserException;
import com.platformone.payment.exception.InsufficientBalanceException;
import com.platformone.payment.exception.WalletNotFoundException;
import com.platformone.payment.jwt.CustomAccessDeniedHandler;
import com.platformone.payment.jwt.JwtAuthenticationEntryPoint;
import com.platformone.payment.jwt.JwtUtils;
import com.platformone.payment.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@Import(SecurityConfig.class)
class WalletControllerIntegrationTest {
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    void setup() {
        wallet = new Wallet(10L, 1000.0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetWalletById_found() throws Exception {
        when(walletService.getWalletById(1L)).thenReturn(wallet);

        mockMvc.perform(get("/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetWalletById_notFound() throws Exception {
        when(walletService.getWalletById(99L)).thenReturn(null);

        mockMvc.perform(get("/wallet/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateWallet_success() throws Exception {
        Wallet savedWallet = new Wallet(10L, 1000.0);

        when(walletService.createWallet(any(Wallet.class))).thenReturn(savedWallet);

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wallet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateWallet_found() throws Exception {
        Wallet updatedWallet = new Wallet(30L, 1500.0);

        when(walletService.updateWallet(eq(1L), any(Wallet.class)))
                .thenReturn(Optional.of(updatedWallet));

        mockMvc.perform(put("/wallet/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(30))
                .andExpect(jsonPath("$.balance").value(1500.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateWallet_notFound() throws Exception {
        Wallet updatedWallet = new Wallet(30L, 1500.0);

        when(walletService.updateWallet(eq(99L), any(Wallet.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/wallet/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteWallet_found() throws Exception {
        when(walletService.deleteWallet(1L)).thenReturn(true);

        mockMvc.perform(delete("/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteWallet_notFound() throws Exception {
        when(walletService.deleteWallet(99L)).thenReturn(false);

        mockMvc.perform(delete("/wallet/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void initializeWallet_shouldReturnCreated() throws Exception {
        WalletCreateRequestDTO requestDTO = new WalletCreateRequestDTO(10L, 1000);

        when(walletService.initializeWallet(any())).thenReturn(wallet);

        mockMvc.perform(post("/wallet/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void initializeWallet_shouldReturnConflictOnDuplicateWallet() throws Exception {
        WalletCreateRequestDTO requestDTO = new WalletCreateRequestDTO(1L, 1000);

        when(walletService.initializeWallet(any()))
                .thenThrow(new DuplicateWalletForUserException());

        mockMvc.perform(post("/wallet/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A wallet for this user already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addFunds_shouldReturnUpdatedWallet() throws Exception {
        WalletTransactionRequestDTO request = new WalletTransactionRequestDTO(6000.0, 0L);

        Wallet updatedWallet = new Wallet(1001L, 6000.0);

        when(walletService.addFunds(eq(1L), any(WalletTransactionRequestDTO.class)))
                .thenReturn(updatedWallet);

        mockMvc.perform(post("/wallet/1/add-funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1001L))
                .andExpect(jsonPath("$.balance").value(6000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addFunds_shouldReturn404IfWalletNotFound() throws Exception {
        WalletTransactionRequestDTO request = new WalletTransactionRequestDTO(1000.0, 0L);

        when(walletService.addFunds(eq(99L), any(WalletTransactionRequestDTO.class)))
                .thenThrow(new WalletNotFoundException("Wallet not found with id 99"));

        mockMvc.perform(post("/wallet/99/add-funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void withdrawFunds_shouldReturnUpdatedWallet() throws Exception {
        WalletTransactionRequestDTO request = new WalletTransactionRequestDTO(2000.0, 0L);
        Wallet updatedWallet = new Wallet(1001L, 3000.0);

        when(walletService.withdrawFunds(eq(1L), any(WalletTransactionRequestDTO.class)))
                .thenReturn(updatedWallet);

        mockMvc.perform(post("/wallet/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(3000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void withdrawFunds_shouldReturn400IfInsufficientBalance() throws Exception {
        WalletTransactionRequestDTO request = new WalletTransactionRequestDTO(10000.0, 0L);

        when(walletService.withdrawFunds(eq(1L), any(WalletTransactionRequestDTO.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance for withdrawal"));

        mockMvc.perform(post("/wallet/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWalletByUserId_shouldReturnWallet() throws Exception {
        when(walletService.getWalletByUserId(1001L)).thenReturn(wallet);

        mockMvc.perform(get("/wallet/user/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWalletByUserId_shouldReturn404IfNotFound() throws Exception {
        when(walletService.getWalletByUserId(9999L))
                .thenThrow(new WalletNotFoundException("Wallet not found for userId 9999"));

        mockMvc.perform(get("/wallet/user/9999"))
                .andExpect(status().isNotFound());
    }
}
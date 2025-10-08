package com.platformone.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Test
    void testGetWalletById_found() throws Exception {
        Wallet wallet = new Wallet(10L, 1000.0);

        when(walletService.getWalletById(1L)).thenReturn(wallet);

        mockMvc.perform(get("/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void testGetWalletById_notFound() throws Exception {
        when(walletService.getWalletById(99L)).thenReturn(null);

        mockMvc.perform(get("/wallet/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateWallet_success() throws Exception {
        Wallet wallet = new Wallet(20L, 500.0);
        Wallet savedWallet = new Wallet(20L, 500.0);

        when(walletService.createWallet(any(Wallet.class))).thenReturn(savedWallet);

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wallet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(20))
                .andExpect(jsonPath("$.balance").value(500.0));
    }

    @Test
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
    void testDeleteWallet_found() throws Exception {
        when(walletService.deleteWallet(1L)).thenReturn(true);

        mockMvc.perform(delete("/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet deleted successfully"));
    }

    @Test
    void testDeleteWallet_notFound() throws Exception {
        when(walletService.deleteWallet(99L)).thenReturn(false);

        mockMvc.perform(delete("/wallet/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }
}
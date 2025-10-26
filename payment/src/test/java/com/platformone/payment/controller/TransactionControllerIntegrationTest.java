package com.platformone.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.payment.config.SecurityConfig;
import com.platformone.payment.entity.Transaction;
import com.platformone.payment.entity.TransactionType;
import com.platformone.payment.exception.WalletNotFoundException;
import com.platformone.payment.jwt.CustomAccessDeniedHandler;
import com.platformone.payment.jwt.JwtAuthenticationEntryPoint;
import com.platformone.payment.jwt.JwtUtils;
import com.platformone.payment.service.TransactionService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerIntegrationTest {
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
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        transaction = new Transaction(1L, 100L, 500.0, TransactionType.CREDIT);
        transaction.setCreationTimeStamp();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTransactionById_Success() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(get("/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(1))
                .andExpect(jsonPath("$.paymentId").value(100))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.transactionType").value("CREDIT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTransactionById_NotFound() throws Exception {
        when(transactionService.getTransactionById(99L)).thenReturn(null);

        mockMvc.perform(get("/transaction/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTransaction() throws Exception {
        Transaction transaction = new Transaction(1L, 100L, 200.0, TransactionType.DEBIT);
        Transaction savedTransaction = new Transaction(1L, 100L, 200.0, TransactionType.DEBIT);

        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(savedTransaction);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.walletId").value(1))
                .andExpect(jsonPath("$.paymentId").value(100))
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.transactionType").value("DEBIT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTransaction_Success() throws Exception {
        Transaction updatedTransaction = new Transaction(2L, 200L, 750.0, TransactionType.CREDIT);

        when(transactionService.updateTransaction(eq(1L), any(Transaction.class)))
                .thenReturn(Optional.of(updatedTransaction));

        mockMvc.perform(put("/transaction/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(2))
                .andExpect(jsonPath("$.paymentId").value(200))
                .andExpect(jsonPath("$.amount").value(750.0))
                .andExpect(jsonPath("$.transactionType").value("CREDIT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTransaction_NotFound() throws Exception {
        Transaction updatedTransaction = new Transaction(2L, 200L, 750.0, TransactionType.CREDIT);

        when(transactionService.updateTransaction(eq(99L), any(Transaction.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/transaction/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTransaction)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTransaction_Success() throws Exception {
        when(transactionService.deleteTransaction(1L)).thenReturn(true);

        mockMvc.perform(delete("/transaction/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTransaction_NotFound() throws Exception {
        when(transactionService.deleteTransaction(99L)).thenReturn(false);

        mockMvc.perform(delete("/transaction/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTransactionsByWallet_Success() throws Exception {
        List<Transaction> transactions = List.of(transaction);

        when(transactionService.getTransactionsByWalletId(1001L)).thenReturn(transactions);

        mockMvc.perform(get("/transaction/wallet/{walletId}", 1001L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].walletId").value(transaction.getWalletId()))
                .andExpect(jsonPath("$[0].paymentId").value(transaction.getPaymentId()))
                .andExpect(jsonPath("$[0].amount").value(transaction.getAmount()))
                .andExpect(jsonPath("$[0].transactionType").value(transaction.getTransactionType().name()));

        verify(transactionService, times(1)).getTransactionsByWalletId(1001L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTransactionsByWallet_WalletNotFound() throws Exception {
        long walletId = 2002L;

        when(transactionService.getTransactionsByWalletId(walletId))
                .thenThrow(new WalletNotFoundException("Wallet not found with id: " + walletId));

        mockMvc.perform(get("/transaction/wallet/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet not found with id: " + walletId));

        verify(transactionService, times(1)).getTransactionsByWalletId(walletId);
    }
}
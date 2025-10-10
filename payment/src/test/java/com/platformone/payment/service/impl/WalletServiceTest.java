package com.platformone.payment.service.impl;

import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Transaction;
import com.platformone.payment.entity.TransactionType;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.exception.DuplicateWalletForUserException;
import com.platformone.payment.exception.InsufficientBalanceException;
import com.platformone.payment.exception.WalletNotFoundException;
import com.platformone.payment.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private TransactionServiceImpl transactionService;

    private Wallet existingWallet;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        existingWallet = new Wallet(1001L, 5000.0);
        existingWallet.setCreationTimeStamp();
    }

    @Test
    void testFindWalletById_WalletExists() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));

        Wallet found = walletService.getWalletById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo(1001L);
        assertThat(found.getBalance()).isEqualTo(5000.0);
        verify(walletRepository).findById(1L);
    }

    @Test
    void testFindWalletById_WalletDoesNotExist() {
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        Wallet found = walletService.getWalletById(1L);

        assertThat(found).isNull();
        verify(walletRepository).findById(1L);
    }

    @Test
    void testCreateWallet() {
        when(walletRepository.save(any(Wallet.class))).thenReturn(existingWallet);

        Wallet created = walletService.createWallet(existingWallet);

        assertThat(created).isNotNull();
        assertThat(created.getBalance()).isEqualTo(5000.0);
        verify(walletRepository).save(existingWallet);
    }

    @Test
    void testUpdateWallet_WalletExists() {
        Wallet updatedData = new Wallet(2002L, 7500.0);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Wallet> result = walletService.updateWallet(1L, updatedData);

        assertThat(result).isPresent();
        Wallet updated = result.get();

        assertThat(updated.getUserId()).isEqualTo(2002L);
        assertThat(updated.getBalance()).isEqualTo(7500.0);

        verify(walletRepository).findById(1L);
        verify(walletRepository).save(existingWallet);
    }

    @Test
    void testUpdateWallet_WalletDoesNotExist() {
        Wallet updatedData = new Wallet(2002L, 7500.0);
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Wallet> result = walletService.updateWallet(1L, updatedData);

        assertThat(result).isEmpty();
        verify(walletRepository, never()).save(any());
    }

    @Test
    void testDeleteWallet_WalletExists() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));

        boolean deleted = walletService.deleteWallet(1L);

        assertThat(deleted).isTrue();
        verify(walletRepository).deleteById(1L);
    }

    @Test
    void testDeleteWallet_WalletDoesNotExist() {
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = walletService.deleteWallet(1L);

        assertThat(deleted).isFalse();
        verify(walletRepository, never()).deleteById(anyLong());
    }

    @Test
    void initializeWallet_shouldThrowExceptionIfWalletExists() {
        WalletCreateRequestDTO requestDTO = new WalletCreateRequestDTO(1L, 1000L);
        when(walletRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(DuplicateWalletForUserException.class, () -> walletService.initializeWallet(requestDTO));
        verify(walletRepository, never()).save(any());
    }

    @Test
    void initializeWallet_shouldSaveWalletIfNotExists() {
        WalletCreateRequestDTO requestDTO = new WalletCreateRequestDTO(1L, 1000L);
        when(walletRepository.existsByUserId(1L)).thenReturn(false);

        Wallet savedWallet = new Wallet(1L, 1000L);
        when(walletRepository.save(any())).thenReturn(savedWallet);

        Wallet result = walletService.initializeWallet(requestDTO);
        assertEquals(1L, result.getUserId());
        assertEquals(1000L, result.getBalance());
        verify(walletRepository, times(1)).save(any());
    }

    @Test
    void addFunds_shouldIncreaseBalanceAndCreateTransaction() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(1000.0,0L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Wallet updated = walletService.addFunds(1L, requestDTO);

        assertThat(updated.getBalance()).isEqualTo(6000.0);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionService).createTransaction(any(Transaction.class));
    }

    @Test
    void addFunds_shouldThrowExceptionIfWalletNotFound() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(1000.0,0L);

        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.addFunds(99L, requestDTO))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("Wallet not found with id 99");
    }

    @Test
    void withdrawFunds_shouldDecreaseBalanceAndCreateTransaction() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(2000.0,0L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Wallet updated = walletService.withdrawFunds(1L, requestDTO);

        assertThat(updated.getBalance()).isEqualTo(3000.0);
        verify(transactionService).createTransaction(any(Transaction.class));
    }

    @Test
    void withdrawFunds_shouldThrowInsufficientBalanceException() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(10000.0,0L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));

        assertThatThrownBy(() -> walletService.withdrawFunds(1L, requestDTO))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance for withdrawal");

        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    void debit_shouldDecreaseBalanceAndCreateTransactionWithPaymentId() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(1500.0,123L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Wallet updated = walletService.debit(1L, requestDTO);

        assertThat(updated.getBalance()).isEqualTo(3500.0);
        verify(transactionService).createTransaction(argThat(tx ->
                tx.getPaymentId() == 123L &&
                        tx.getTransactionType() == TransactionType.DEBIT
        ));
    }

    @Test
    void debit_shouldThrowInsufficientBalanceException() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(10000.0,123L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));

        assertThatThrownBy(() -> walletService.debit(1L, requestDTO))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance for debit");
    }

    @Test
    void credit_shouldIncreaseBalanceAndCreateTransactionWithPaymentId() {
        WalletTransactionRequestDTO requestDTO = new WalletTransactionRequestDTO(500.0,321L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Wallet updated = walletService.credit(1L, requestDTO);

        assertThat(updated.getBalance()).isEqualTo(5500.0);
        verify(transactionService).createTransaction(argThat(tx ->
                tx.getPaymentId() == 321L &&
                        tx.getTransactionType() == TransactionType.CREDIT
        ));
    }

    @Test
    void getWalletByUserId_shouldReturnWallet() {
        when(walletRepository.findByUserId(1001L)).thenReturn(Optional.of(existingWallet));

        Wallet found = walletService.getWalletByUserId(1001L);

        assertThat(found).isEqualTo(existingWallet);
    }

    @Test
    void getWalletByUserId_shouldThrowExceptionIfNotFound() {
        when(walletRepository.findByUserId(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getWalletByUserId(9999L))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("Wallet not found for userId 9999");
    }
}
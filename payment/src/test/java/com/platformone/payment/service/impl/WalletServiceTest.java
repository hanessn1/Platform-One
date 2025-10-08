package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Wallet;
import com.platformone.payment.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

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
}
package com.platformone.payment.service;

import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.dto.WalletTransactionRequestDTO;
import com.platformone.payment.entity.Wallet;

import java.util.Optional;

public interface WalletService {
    Wallet getWalletById(long walletId);

    Wallet createWallet(Wallet newWallet);

    Wallet initializeWallet(WalletCreateRequestDTO requestDTO);

    Optional<Wallet> updateWallet(long walletId, Wallet updatedWallet);

    boolean deleteWallet(long walletId);

    Wallet addFunds(long walletId, WalletTransactionRequestDTO requestDTO);

    Wallet withdrawFunds(long walletId, WalletTransactionRequestDTO requestDTO);

    Wallet debit(long walletId, WalletTransactionRequestDTO requestDTO);

    Wallet credit(long walletId, WalletTransactionRequestDTO requestDTO);

    Wallet getWalletByUserId(long userId);
}
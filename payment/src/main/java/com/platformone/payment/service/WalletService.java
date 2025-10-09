package com.platformone.payment.service;

import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.entity.Wallet;

import java.util.Optional;

public interface WalletService {
    Wallet getWalletById(long walletId);

    Wallet createWallet(Wallet newWallet);

    Wallet initializeWallet(WalletCreateRequestDTO requestDTO);

    Optional<Wallet> updateWallet(long walletId, Wallet updatedWallet);

    boolean deleteWallet(long walletId);
}

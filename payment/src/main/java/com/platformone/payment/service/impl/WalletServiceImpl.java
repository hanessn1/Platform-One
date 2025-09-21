package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Wallet;
import com.platformone.payment.repository.WalletRepository;
import com.platformone.payment.service.WalletService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet getWalletById(long walletId) {
        return walletRepository.findById(walletId).orElse(null);
    }

    @Override
    public Wallet createWallet(Wallet newWallet) {
        return walletRepository.save(newWallet);
    }

    @Override
    public Optional<Wallet> updateWallet(long walletId, Wallet updatedWallet) {
        return walletRepository.findById(walletId).map(wallet -> {
            wallet.setBalance(updatedWallet.getBalance());
            wallet.setUserId(updatedWallet.getUserId());
            return walletRepository.save(wallet);
        });
    }

    @Override
    public boolean deleteWallet(long walletId) {
        Wallet wallet = getWalletById(walletId);
        if (wallet == null) return false;
        walletRepository.deleteById(walletId);
        return true;
    }
}

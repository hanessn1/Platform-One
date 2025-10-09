package com.platformone.payment.service.impl;

import com.platformone.payment.dto.WalletCreateRequestDTO;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.exception.DuplicateWalletForUserException;
import com.platformone.payment.repository.WalletRepository;
import com.platformone.payment.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);
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
        if (walletRepository.existsByUserId(newWallet.getUserId())) {
            throw new DuplicateWalletForUserException();
        }
        return walletRepository.save(newWallet);
    }

    @Override
    public Wallet initializeWallet(WalletCreateRequestDTO requestDTO) {
        if (walletRepository.existsByUserId(requestDTO.getUserId())) {
            throw new DuplicateWalletForUserException();
        }
        log.debug("InitializeWallet request: userId={}, balance={}", requestDTO.getUserId(), requestDTO.getBalance());
        Wallet wallet = new Wallet(requestDTO.getUserId(), requestDTO.getBalance());
        return walletRepository.save(wallet);
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
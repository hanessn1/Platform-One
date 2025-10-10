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
import com.platformone.payment.service.TransactionService;
import com.platformone.payment.service.WalletService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public WalletServiceImpl(WalletRepository walletRepository1, TransactionService transactionService1) {
        this.walletRepository = walletRepository1;
        this.transactionService = transactionService1;
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

    @Override
    public Wallet addFunds(long walletId, WalletTransactionRequestDTO requestDTO) {
        Wallet wallet = getWalletOrThrow(walletId);

        wallet.setBalance(wallet.getBalance() + requestDTO.getAmount());
        walletRepository.save(wallet);

        Transaction walletTransaction = new Transaction(walletId, requestDTO.getAmount(), TransactionType.CREDIT);
        transactionService.createTransaction(walletTransaction);

        log.debug("Added {} to walletId={}, newBalance={}", requestDTO.getAmount(), walletId, wallet.getBalance());
        return wallet;
    }

    private Wallet getWalletOrThrow(long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id " + walletId));
    }

    @Override
    public Wallet withdrawFunds(long walletId, WalletTransactionRequestDTO requestDTO) {
        Wallet wallet = getWalletOrThrow(walletId);

        if (wallet.getBalance() < requestDTO.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance() - requestDTO.getAmount());
        walletRepository.save(wallet);

        Transaction walletTransaction = new Transaction(walletId, requestDTO.getAmount(), TransactionType.DEBIT);
        transactionService.createTransaction(walletTransaction);

        log.debug("Withdrew {} from walletId={}, newBalance={}", requestDTO.getAmount(), walletId, wallet.getBalance());
        return wallet;
    }

    @Override
    public Wallet debit(long walletId, WalletTransactionRequestDTO requestDTO) {
        Wallet wallet = getWalletOrThrow(walletId);

        if (wallet.getBalance() < requestDTO.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance for debit");
        }

        wallet.setBalance(wallet.getBalance() - requestDTO.getAmount());
        walletRepository.save(wallet);

        Transaction walletTransaction = new Transaction(walletId, requestDTO.getPaymentId(), requestDTO.getAmount(), TransactionType.DEBIT);
        transactionService.createTransaction(walletTransaction);

        log.debug("Debited {} from walletId={} for booking/payment paymentId={}, newBalance={}", requestDTO.getAmount(), walletId, requestDTO.getPaymentId(), wallet.getBalance());
        return wallet;
    }

    @Override
    public Wallet credit(long walletId, WalletTransactionRequestDTO requestDTO) {
        Wallet wallet = getWalletOrThrow(walletId);

        wallet.setBalance(wallet.getBalance() + requestDTO.getAmount());
        walletRepository.save(wallet);

        Transaction walletTransaction = new Transaction(walletId, requestDTO.getPaymentId(), requestDTO.getAmount(), TransactionType.CREDIT);
        transactionService.createTransaction(walletTransaction);

        log.debug("Credited {} to walletId={} for refund, paymentId={}, newBalance={}", requestDTO.getAmount(), walletId, requestDTO.getPaymentId(), wallet.getBalance());
        return wallet;
    }

    @Override
    public Wallet getWalletByUserId(long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for userId " + userId));
    }
}
package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Transaction;
import com.platformone.payment.entity.Wallet;
import com.platformone.payment.exception.WalletNotFoundException;
import com.platformone.payment.repository.TransactionRepository;
import com.platformone.payment.repository.WalletRepository;
import com.platformone.payment.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, WalletRepository walletRepository1) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository1;
    }

    @Override
    public Transaction getTransactionById(long transactionId) {
        return transactionRepository.findById(transactionId).orElse(null);
    }

    @Override
    public Transaction createTransaction(Transaction newTransaction) {
        return transactionRepository.save(newTransaction);
    }

    @Override
    public Optional<Transaction> updateTransaction(long transactionId, Transaction updatedTransaction) {
        return transactionRepository.findById(transactionId).map(transaction -> {
            transaction.setAmount(updatedTransaction.getAmount());
            transaction.setPaymentId(updatedTransaction.getPaymentId());
            transaction.setTransactionType(updatedTransaction.getTransactionType());
            transaction.setWalletId(updatedTransaction.getWalletId());
            return transactionRepository.save(transaction);
        });
    }

    @Override
    public boolean deleteTransaction(long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        if (transaction == null) return false;
        transactionRepository.deleteById(transactionId);
        return true;
    }

    @Override
    public List<Transaction> getTransactionsByWalletId(long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));

        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }
}
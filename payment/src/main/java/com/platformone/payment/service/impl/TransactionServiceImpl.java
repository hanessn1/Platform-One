package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Transaction;
import com.platformone.payment.repository.TransactionRepository;
import com.platformone.payment.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
}

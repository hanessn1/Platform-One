package com.platformone.payment.service;

import com.platformone.payment.entity.Transaction;

import java.util.Optional;

public interface TransactionService {
    Transaction getTransactionById(long transactionId);

    Transaction createTransaction(Transaction newTransaction);

    Optional<Transaction> updateTransaction(long transactionId, Transaction updatedTransaction);

    boolean deleteTransaction(long transactionId);
}

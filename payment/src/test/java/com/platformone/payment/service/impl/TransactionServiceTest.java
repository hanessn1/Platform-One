package com.platformone.payment.service.impl;

import com.platformone.payment.entity.Transaction;
import com.platformone.payment.entity.TransactionType;
import com.platformone.payment.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction existingTransaction;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        existingTransaction = new Transaction(1001L, 2001L, 500.0, TransactionType.CREDIT);
        existingTransaction.setCreationTimeStamp();
    }

    @Test
    void testFindTransactionById_TransactionExists() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));

        Transaction found = transactionService.getTransactionById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getWalletId()).isEqualTo(1001L);
        assertThat(found.getPaymentId()).isEqualTo(2001L);
        assertThat(found.getTransactionType()).isEqualTo(TransactionType.CREDIT);
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testFindTransactionById_TransactionDoesNotExist() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Transaction found = transactionService.getTransactionById(1L);

        assertThat(found).isNull();
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testCreateTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        Transaction created = transactionService.createTransaction(existingTransaction);

        assertThat(created).isNotNull();
        assertThat(created.getAmount()).isEqualTo(500.0);
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void testUpdateTransaction_TransactionExists() {
        Transaction updatedData = new Transaction(3003L, 4004L, 900.0, TransactionType.DEBIT);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Transaction> result = transactionService.updateTransaction(1L, updatedData);

        assertThat(result).isPresent();
        Transaction updated = result.get();

        assertThat(updated.getWalletId()).isEqualTo(3003L);
        assertThat(updated.getPaymentId()).isEqualTo(4004L);
        assertThat(updated.getAmount()).isEqualTo(900.0);
        assertThat(updated.getTransactionType()).isEqualTo(TransactionType.DEBIT);

        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void testUpdateTransaction_TransactionDoesNotExist() {
        Transaction updatedData = new Transaction(3003L, 4004L, 900.0, TransactionType.DEBIT);
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.updateTransaction(1L, updatedData);

        assertThat(result).isEmpty();
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testDeleteTransaction_TransactionExists() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existingTransaction));

        boolean deleted = transactionService.deleteTransaction(1L);

        assertThat(deleted).isTrue();
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_TransactionDoesNotExist() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = transactionService.deleteTransaction(1L);

        assertThat(deleted).isFalse();
        verify(transactionRepository, never()).deleteById(anyLong());
    }
}
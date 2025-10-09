package com.platformone.payment.repository;

import com.platformone.payment.entity.Transaction;
import com.platformone.payment.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();

        transaction = new Transaction(1001L, 501L, 250.0, TransactionType.CREDIT);
        transactionRepository.save(transaction);
    }

    @Test
    void testFindById() {
        Optional<Transaction> found = transactionRepository.findById(transaction.getTransactionId());

        assertThat(found).isPresent();
        assertThat(found.get().getWalletId()).isEqualTo(1001L);
        assertThat(found.get().getPaymentId()).isEqualTo(501L);
        assertThat(found.get().getAmount()).isEqualTo(250.0);
        assertThat(found.get().getTransactionType()).isEqualTo(TransactionType.CREDIT);
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void testSaveAnotherTransaction() {
        Transaction another = new Transaction(2002L, 601L, 400.0, TransactionType.DEBIT);
        Transaction saved = transactionRepository.save(another);

        assertThat(saved.getTransactionId()).isGreaterThan(0);
        assertThat(saved.getWalletId()).isEqualTo(2002L);
        assertThat(saved.getPaymentId()).isEqualTo(601L);
        assertThat(saved.getAmount()).isEqualTo(400.0);
        assertThat(saved.getTransactionType()).isEqualTo(TransactionType.DEBIT);
        assertThat(saved.getCreatedAt()).isNotNull();

        long totalCount = transactionRepository.count();
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    void testDeleteTransaction() {
        transactionRepository.delete(transaction);

        Optional<Transaction> deleted = transactionRepository.findById(transaction.getTransactionId());
        assertThat(deleted).isNotPresent();

        assertThat(transactionRepository.count()).isZero();
    }
}
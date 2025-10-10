package com.platformone.payment.repository;

import com.platformone.payment.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();

        wallet = new Wallet(1001L, 1000.0);
        walletRepository.save(wallet);
    }

    @Test
    void testFindById() {
        Optional<Wallet> found = walletRepository.findById(wallet.getWalletId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1001L);
        assertThat(found.get().getBalance()).isEqualTo(1000.0);
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void testSaveAnotherWallet_andFindByUserId() {
        Wallet another = new Wallet(2002L, 2500.0);
        walletRepository.save(another);

        Optional<Wallet> found = walletRepository.findByUserId(2002L);
        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualTo(2500.0);

        long totalCount = walletRepository.count();
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    void testDeleteWallet_andCheckExistsByUserId() {
        walletRepository.delete(wallet);

        Optional<Wallet> deleted = walletRepository.findByUserId(1001L);
        assertThat(deleted).isNotPresent();

        boolean exists = walletRepository.existsByUserId(1001L);
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUserId_shouldReturnTrueIfWalletExists() {
        Wallet wallet = new Wallet(1L, 1000L);
        walletRepository.save(wallet);

        boolean exists = walletRepository.existsByUserId(1L);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserId_shouldReturnFalseIfWalletDoesNotExist() {
        boolean exists = walletRepository.existsByUserId(999L);
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByUserId_existingWallet() {
        Optional<Wallet> found = walletRepository.findByUserId(1001L);

        assertThat(found).isPresent();
        assertThat(found.get().getWalletId()).isEqualTo(wallet.getWalletId());
        assertThat(found.get().getBalance()).isEqualTo(1000.0);
    }

    @Test
    void testFindByUserId_nonExistingWallet() {
        Optional<Wallet> found = walletRepository.findByUserId(9999L);

        assertThat(found).isNotPresent();
    }
}
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
    void testSaveAnotherWallet() {
        Wallet another = new Wallet(2002L, 2500.0);
        Wallet saved = walletRepository.save(another);

        assertThat(saved.getWalletId()).isGreaterThan(0);
        assertThat(saved.getUserId()).isEqualTo(2002L);
        assertThat(saved.getBalance()).isEqualTo(2500.0);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        long totalCount = walletRepository.count();
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    void testDeleteWallet() {
        walletRepository.delete(wallet);

        Optional<Wallet> deleted = walletRepository.findById(wallet.getWalletId());
        assertThat(deleted).isNotPresent();
        assertThat(walletRepository.count()).isZero();
    }
}
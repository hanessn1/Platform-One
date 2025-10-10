package com.platformone.payment.repository;

import com.platformone.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    boolean existsByUserId(long userId);

    Optional<Wallet> findByUserId(long userId);
}

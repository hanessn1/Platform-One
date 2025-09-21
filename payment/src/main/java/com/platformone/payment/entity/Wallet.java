package com.platformone.payment.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "Wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long walletId;

    private long userId;
    private double balance;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(updatable = true)
    private Instant updatedAt;

    protected Wallet() {
    }

    public Wallet(long userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public long getWalletId() {
        return walletId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    public void setCreationTimeStamp(){
        Instant instant=Instant.now();
        this.createdAt=instant;
        this.updatedAt=instant;
    }

    @PreUpdate
    public void setUpdationTimeStamp(){
        this.updatedAt=Instant.now();
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

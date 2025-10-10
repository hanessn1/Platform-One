package com.platformone.payment.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    @Column(nullable = false, updatable = false)
    private long walletId;

    @Column(nullable = true, updatable = false)
    private long paymentId;

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    protected Transaction() {
    }

    public Transaction(long walletId, long paymentId, double amount, TransactionType transactionType) {
        this.walletId = walletId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public Transaction(long walletId, double amount,TransactionType transactionType){
        this.walletId = walletId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    public void setCreationTimeStamp() {
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", walletId=" + walletId +
                ", paymentId=" + paymentId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", createdAt=" + createdAt +
                '}';
    }
}

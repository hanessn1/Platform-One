package com.platformone.payment.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;

    @Column(nullable = false, updatable = false)
    private long bookingId;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatusType paymentStatusType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Payment() {
    }

    public Payment(long bookingId, double amount, PaymentStatusType paymentStatusType) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentStatusType = paymentStatusType;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatusType getPaymentStatusType() {
        return paymentStatusType;
    }

    public void setPaymentStatusType(PaymentStatusType paymentStatusType) {
        this.paymentStatusType = paymentStatusType;
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
        return "Payment{" +
                "paymentId=" + paymentId +
                ", bookingId=" + bookingId +
                ", amount=" + amount +
                ", paymentStatusType=" + paymentStatusType +
                ", createdAt=" + createdAt +
                '}';
    }
}

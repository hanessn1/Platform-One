package com.platformone.user.dto;

public class WalletCreateRequestDTO {
    private long userId;
    private double balance;

    public WalletCreateRequestDTO(long userId, double balance) {
        this.userId = userId;
        this.balance = balance;
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

    @Override
    public String toString() {
        return "WalletCreateRequestDTO{" +
                "userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
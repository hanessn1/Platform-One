package com.platformone.user.external;

public class Wallet {
    private long walletId;
    private long userId;
    private double balance;

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

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
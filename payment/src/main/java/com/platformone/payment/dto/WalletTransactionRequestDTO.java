package com.platformone.payment.dto;

public class WalletTransactionRequestDTO {
    private Double amount;
    private Long paymentId;

    public WalletTransactionRequestDTO(Double amount, Long paymentId) {
        this.amount = amount;
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return "WalletTransactionRequestDTO{" +
                "amount=" + amount +
                ", paymentId=" + paymentId +
                '}';
    }
}

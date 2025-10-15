package com.platformone.booking.events;

public class PaymentRefundedEvent extends BaseEvent {
    private long bookingId;
    private long paymentId;
    private double refundAmount;

    public PaymentRefundedEvent() {
    }

    public PaymentRefundedEvent(long bookingId, long paymentId, double refundAmount) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Override
    public String toString() {
        return "PaymentRefundedEvent{" +
                "bookingId=" + bookingId +
                ", paymentId=" + paymentId +
                ", refundAmount=" + refundAmount +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}
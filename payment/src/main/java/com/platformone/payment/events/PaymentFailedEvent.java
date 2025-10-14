package com.platformone.payment.events;

public class PaymentFailedEvent extends BaseEvent {
    private long bookingId;
    private long paymentId;
    private String reason;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(long bookingId, long paymentId, String reason) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "bookingId=" + bookingId +
                ", paymentId=" + paymentId +
                ", reason='" + reason + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}
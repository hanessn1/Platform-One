package com.platformone.payment.events;

public class PaymentSucceededEvent extends BaseEvent {
    private long bookingId;
    private long paymentId;

    public PaymentSucceededEvent() {
    }

    public PaymentSucceededEvent(long bookingId, long paymentId) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
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

    @Override
    public String toString() {
        return "PaymentSucceededEvent{" +
                "paymentId=" + paymentId +
                ", bookingId=" + bookingId +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}
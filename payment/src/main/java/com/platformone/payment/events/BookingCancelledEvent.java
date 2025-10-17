package com.platformone.payment.events;

public class BookingCancelledEvent extends BaseEvent {
    private long bookingId;
    private long userId;
    private long scheduleId;
    private double fareAmount;

    public BookingCancelledEvent() {
    }

    public BookingCancelledEvent(long bookingId, long userId, long scheduleId, double fareAmount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.fareAmount = fareAmount;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public double getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
    }

    @Override
    public String toString() {
        return "BookingCancelledEvent{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", fareAmount=" + fareAmount +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}
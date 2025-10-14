package com.platformone.booking.dto;

public class BookingRequestDTO {
    private long userId;
    private long scheduleId;

    public BookingRequestDTO() {
    }

    public BookingRequestDTO(long userId, long scheduleId, double fareAmount) {
        this.userId = userId;
        this.scheduleId = scheduleId;
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

    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "userId=" + userId +
                ", scheduleId=" + scheduleId +
                '}';
    }
}
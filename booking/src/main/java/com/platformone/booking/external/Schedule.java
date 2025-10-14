package com.platformone.booking.external;

import java.time.Instant;
import java.time.LocalDate;

public class Schedule {
    private long scheduleId;
    private long trainId;
    private LocalDate scheduleDate;
    private int totalSeats;
    private int availableSeats;
    private Instant createdAt;
    private double fareAmount;

    public Schedule(long trainId, LocalDate scheduleDate, int totalSeats, int availableSeats, double fareAmount) {
        this.trainId = trainId;
        this.scheduleDate = scheduleDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.fareAmount = fareAmount;
    }

    public Schedule() {
    }

    public void setCreationTimestamp() {
        this.createdAt = Instant.now();
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public long getTrainId() {
        return trainId;
    }

    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", trainId=" + trainId +
                ", scheduleDate=" + scheduleDate +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", fareAmount=" + fareAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
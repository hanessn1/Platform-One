package com.platformone.schedule.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long scheduleId;

    private long trainId;
    private LocalDate journeyDate;
    private int totalSeats;
    private int availableSeats;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Schedule(long trainId, LocalDate journeyDate, int totalSeats, int availableSeats) {
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public Schedule() {
    }

    @PrePersist
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

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", trainId=" + trainId +
                ", journeyDate=" + journeyDate +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", createdAt=" + createdAt +
                '}';
    }
}
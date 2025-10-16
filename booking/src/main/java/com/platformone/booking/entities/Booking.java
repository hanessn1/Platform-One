package com.platformone.booking.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingId;

    private long userId;
    private long scheduleId;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private int seatNumber;
    private String pnr;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(updatable = true, nullable = false)
    private Instant updatedAt;

    protected Booking() {
    }

    public Booking(long userId, long scheduleId, BookingStatus bookingStatus, int seatNumber, String pnr) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.bookingStatus = bookingStatus;
        this.seatNumber = seatNumber;
        this.pnr = pnr;
    }

    public long getBookingId() {
        return bookingId;
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

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    public void setCreationTimestamp(){
        Instant now=Instant.now();
        this.createdAt=now;
        this.updatedAt=now;
    }

    @PreUpdate
    public void setUpdationTimestamp(){
        this.updatedAt=Instant.now();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", bookingStatus=" + bookingStatus +
                ", seatNumber=" + seatNumber +
                ", pnr='" + pnr + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
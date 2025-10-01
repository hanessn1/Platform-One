package com.platformone.booking.dto;

import com.platformone.booking.entities.BookingStatus;
import com.platformone.booking.external.TrainType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    private String pnr;
    private BookingStatus bookingStatus;
    private int seatNumber;
    private double fareAmount;
    private LocalDate bookingDate;

    // Train info
    private String trainName;
    private TrainType trainType;

    // journey info
    private String sourceStationCode;
    private String sourceStationName;
    private LocalDateTime departureTime;

    private String destinationStationCode;
    private String destinationStationName;
    private LocalDateTime arrivalTime;

    private LocalDate journeyDate;

    public BookingResponseDTO() {
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
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

    public double getFareAmount() {
        return fareAmount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public TrainType getTrainType() {
        return trainType;
    }

    public void setTrainType(TrainType trainType) {
        this.trainType = trainType;
    }

    public String getSourceStationCode() {
        return sourceStationCode;
    }

    public void setSourceStationCode(String sourceStationCode) {
        this.sourceStationCode = sourceStationCode;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public void setSourceStationName(String sourceStationName) {
        this.sourceStationName = sourceStationName;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getDestinationStationCode() {
        return destinationStationCode;
    }

    public void setDestinationStationCode(String destinationStationCode) {
        this.destinationStationCode = destinationStationCode;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    @Override
    public String toString() {
        return "BookingResponseDTO{" +
                "pnr='" + pnr + '\'' +
                ", bookingStatus=" + bookingStatus +
                ", seatNumber=" + seatNumber +
                ", fareAmount=" + fareAmount +
                ", bookingDate=" + bookingDate +
                ", trainName='" + trainName + '\'' +
                ", trainType=" + trainType +
                ", sourceStationCode='" + sourceStationCode + '\'' +
                ", sourceStationName='" + sourceStationName + '\'' +
                ", departureTime=" + departureTime +
                ", destinationStationCode='" + destinationStationCode + '\'' +
                ", destinationStationName='" + destinationStationName + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", journeyDate=" + journeyDate +
                '}';
    }
}

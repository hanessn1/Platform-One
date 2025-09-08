package com.platformone.train.dto;

import java.time.LocalDateTime;

public class RouteUpdateRequest {
    private long trainId;
    private long stationId;
    private int sequenceNum;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    public RouteUpdateRequest(long trainId, long stationId, int sequenceNum, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.trainId = trainId;
        this.stationId = stationId;
        this.sequenceNum = sequenceNum;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public long getTrainId() {
        return trainId;
    }

    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "RouteUpdateRequest{" +
                "trainId=" + trainId +
                ", stationId=" + stationId +
                ", sequenceNum=" + sequenceNum +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                '}';
    }
}

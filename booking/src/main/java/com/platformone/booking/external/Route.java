package com.platformone.booking.external;

import java.time.LocalDateTime;

public class Route {
    private long routeId;
    private Train train;
    private Station station;
    private int sequenceNum;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    protected Route() {
    }

    public Route(int sequenceNum, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.sequenceNum = sequenceNum;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public long getRouteId() {
        return routeId;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
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
        return "Route{" +
                "routeId=" + routeId +
                ", station=" + station +
                ", sequenceNum=" + sequenceNum +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                '}';
    }
}

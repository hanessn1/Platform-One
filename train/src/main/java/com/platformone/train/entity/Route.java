package com.platformone.train.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long routeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "train_id",nullable = false)
    @JsonIgnore
    private Train train;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station_id",nullable = false)
    private Station station;

    @Column(nullable = false)
    private int sequenceNum;

    @Column(nullable = false)
    private Instant arrivalTime;

    @Column(nullable = false)
    private Instant departureTime;

    protected Route() {
    }

    public Route(Train train, Station station, int sequenceNum, Instant arrivalTime, Instant departureTime) {
        this.train = train;
        this.station = station;
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

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
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

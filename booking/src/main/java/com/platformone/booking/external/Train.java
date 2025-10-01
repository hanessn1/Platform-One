package com.platformone.booking.external;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Train {
    private long trainId;
    private String name;
    private TrainType type;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Route> routes = new ArrayList<>();

    protected Train() {
    }

    public Train(String name, TrainType type) {
        this.name = name;
        this.type = type;
    }

    public long getTrainId() {
        return trainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrainType getType() {
        return type;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        routes.add(route);
        route.setTrain(this);
    }

    public void removeRoute(Route route) {
        routes.remove(route);
        route.setTrain(null);
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainId=" + trainId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", routes=" + routes +
                '}';
    }
}

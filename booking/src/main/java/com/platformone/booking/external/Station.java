package com.platformone.booking.external;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private long stationId;
    private String name;
    private String code;
    private String city;
    private String state;
    private List<Route> routes = new ArrayList<>();

    protected Station() {
    }

    public Station(String name, String code, String city, String state) {
        this.name = name;
        this.code = code;
        this.city = city;
        this.state = state;
    }

    public long getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
        route.setStation(this);
    }

    public void removeRoute(Route route) {
        this.routes.remove(route);
        route.setStation(null);
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId=" + stationId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

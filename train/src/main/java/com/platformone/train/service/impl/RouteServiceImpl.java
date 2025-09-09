package com.platformone.train.service.impl;

import com.platformone.train.dto.RouteCreateRequest;
import com.platformone.train.dto.RouteUpdateRequest;
import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.repository.RouteRepository;
import com.platformone.train.service.RouteService;
import com.platformone.train.service.StationService;
import com.platformone.train.service.TrainService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.Integer.max;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;
    private final TrainService trainService;
    private final StationService stationService;

    public RouteServiceImpl(RouteRepository routeRepository, TrainService trainService, StationService stationService) {
        this.routeRepository = routeRepository;
        this.trainService = trainService;
        this.stationService = stationService;
    }

    @Override
    public Route getRouteById(long routeId) {
        return routeRepository.findById(routeId).orElse(null);
    }

    @Override
    public Route createRoute(RouteCreateRequest routeRequest) {
        Train train=trainService.getTrainById(routeRequest.getTrainId());
        Station station=stationService.getStationById(routeRequest.getStationId());

        if(train==null || station==null) return null;
        int maxSequenceNum=0;
        if(!train.getRoutes().isEmpty()){
            for(Route route: train.getRoutes()){
                maxSequenceNum=max(maxSequenceNum,route.getSequenceNum());
            }
        }
        Route route=new Route(
                maxSequenceNum+1,
                routeRequest.getArrivalTime(),
                routeRequest.getDepartureTime()
        );
        route.setTrain(train);
        route.setStation(station);
        return routeRepository.save(route);
    }

    @Override
    public Optional<Route> updateRoute(long routeId, RouteUpdateRequest routeRequest) {
        Train train=trainService.getTrainById(routeRequest.getTrainId());
        Station station=stationService.getStationById(routeRequest.getStationId());

        if(train==null || station==null) return Optional.empty();
        return routeRepository.findById(routeId).map(route -> {
            route.setStation(station);
            route.setTrain(train);
            route.setSequenceNum(routeRequest.getSequenceNum());
            route.setArrivalTime(routeRequest.getArrivalTime());
            route.setDepartureTime(routeRequest.getDepartureTime());
            return routeRepository.save(route);
        });
    }

    @Override
    public boolean deleteRoute(long routeId) {
        Route route=getRouteById(routeId);
        if(route==null) return false;
        routeRepository.deleteById(routeId);
        return true;
    }
}

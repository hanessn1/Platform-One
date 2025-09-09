package com.platformone.train.service.impl;

import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.repository.StationRepository;
import com.platformone.train.service.StationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public Station getStationById(long stationId) {
        return stationRepository.findById(stationId).orElse(null);
    }

    @Override
    public Station createStation(Station newStation) {
        return stationRepository.save(newStation);
    }

    @Override
    public Optional<Station> updateStation(long stationId, Station updatedStation) {
        return stationRepository.findById(stationId).map(station -> {
            station.setName(updatedStation.getName());
            station.setCode(updatedStation.getCode());
            station.setState(updatedStation.getState());
            station.setCity(updatedStation.getCity());
            station.getRoutes().clear();
            if(!updatedStation.getRoutes().isEmpty()){
                for(Route route: updatedStation.getRoutes()){
                    station.addRoute(route);
                }
            }
            return stationRepository.save(station);
        });
    }

    @Override
    public boolean deleteStation(long stationId) {
        Station station=getStationById(stationId);
        if(station==null) return false;
        stationRepository.deleteById(stationId);
        return true;
    }
}

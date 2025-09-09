package com.platformone.train.service;

import com.platformone.train.entity.Station;

import java.util.Optional;

public interface StationService {
    Station getStationById(long stationId);

    Station createStation(Station newStation);

    Optional<Station> updateStation(long stationId, Station updatedStation);

    boolean deleteStation(long stationId);
}

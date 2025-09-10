package com.platformone.train.repository;

import com.platformone.train.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route,Long> {

    List<Route> findByStationStationId(Long stationId);

    List<Route> findByTrainTrainId(Long trainId);
}

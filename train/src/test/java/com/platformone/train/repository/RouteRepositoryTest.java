package com.platformone.train.repository;

import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class RouteRepositoryTest {
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Train train;
    private Station station;
    private Route route;

    @BeforeEach
    void setup() {
        routeRepository.deleteAll();
        trainRepository.deleteAll();
        stationRepository.deleteAll();

        station = stationRepository.save(new Station("Station", "STN", "city", "state"));
        route = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        route.setStation(station);
        train = new Train("Rajdhani Express", TrainType.EXPRESS);
        train.addRoute(route);
        trainRepository.save(train);
        routeRepository.save(route);
    }

    @Test
    void testFindById() {
        Optional<Route> found = routeRepository.findById(route.getRouteId());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getSequenceNum());
        assertEquals(train.getTrainId(), found.get().getTrain().getTrainId());
        assertEquals(station.getStationId(), found.get().getStation().getStationId());
    }

    @Test
    void testSaveAnotherRoute() {
        Station newStation = stationRepository.save(new Station("NEW Station", "NSTN", "city2", "state2"));
        Route newRoute = new Route(2, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(1).plusMinutes(15));
        newRoute.setTrain(train);
        newRoute.setStation(newStation);

        Route saved = routeRepository.saveAndFlush(newRoute);
        assertNotNull(saved.getRouteId());
        assertEquals(2, saved.getSequenceNum());
        assertEquals("NSTN", saved.getStation().getCode());
    }

    @Test
    void testDeleteRoute() {
        routeRepository.delete(route);
        assertFalse(routeRepository.findById(route.getRouteId()).isPresent());
    }

    @Test
    void testFindByStationStationId() {
        List<Route> routes = routeRepository.findByStationStationId(station.getStationId());
        assertNotNull(routes);
        assertInstanceOf(List.class, routes);
        assertNotNull(routes.getFirst());
        assertEquals(1, routes.getFirst().getSequenceNum());
        assertNotNull(routes.getFirst().getStation());
        assertEquals("STN", routes.getFirst().getStation().getCode());
    }

    @Test
    void testFindByTrainTrainId() {
        List<Route> routes = routeRepository.findByTrainTrainId(train.getTrainId());
        assertNotNull(routes);
        assertInstanceOf(List.class, routes);
        assertNotNull(routes.getFirst());
        assertNotNull(routes.getFirst().getTrain());
        assertNotNull(routes.getFirst().getStation());
        assertEquals("Rajdhani Express",routes.getFirst().getTrain().getName());
    }
}

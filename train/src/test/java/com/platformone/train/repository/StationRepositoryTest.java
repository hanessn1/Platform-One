package com.platformone.train.repository;

import com.platformone.train.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Station station;

    @BeforeEach
    void setup(){
        stationRepository.deleteAll();
        routeRepository.deleteAll();
        station=new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh");
        stationRepository.save(station);
    }

    @Test
    void testFindById(){
        Optional<Station> found=stationRepository.findById(station.getStationId());
        assertTrue(found.isPresent());
        assertEquals("Kanpur Central",found.get().getName());
        assertEquals("CNB",found.get().getCode());
        assertEquals("Kanpur",found.get().getCity());
        assertEquals("Uttar Pradesh",found.get().getState());
    }

    @Test
    void testSaveAnotherStation(){
        Station newStation=new Station("Patna Junction", "PNBE", "Patna", "Bihar");
        Station saved=stationRepository.save(newStation);
        assertNotNull(saved.getStationId());
        assertEquals("Patna Junction",saved.getName());
    }

    @Test
    void testDeleteStation(){
        stationRepository.delete(station);
        assertFalse(stationRepository.findById(station.getStationId()).isPresent());
    }
}

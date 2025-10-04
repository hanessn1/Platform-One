package com.platformone.train.service.impl;

import com.platformone.train.dto.RouteCreateRequest;
import com.platformone.train.dto.RouteUpdateRequest;
import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import com.platformone.train.repository.RouteRepository;
import com.platformone.train.service.StationService;
import com.platformone.train.service.TrainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class RouteServiceImplTest {
    @Mock
    private RouteRepository routeRepository;

    @Mock
    private TrainService trainService;

    @Mock
    private StationService stationService;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Train train;
    private Station station;
    private Route route;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        train = new Train("Rajdhani Express", TrainType.EXPRESS);
        station = new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh");
        route = new Route(2, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(1).plusMinutes(15));
        route.setTrain(train);
        route.setStation(station);
    }

    @Test
    void testGetRouteById_RouteExists() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        Route found = routeService.getRouteById(1L);
        assertNotNull(found);
        assertEquals(2, found.getSequenceNum());
        assertNotNull(found.getArrivalTime());
        assertNotNull(found.getRouteId());
    }

    @Test
    void testGetRouteById_RouteDoesNotExist() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());
        Route found = routeService.getRouteById(1L);
        assertNull(found);
    }

    @Test
    void testCreateRoute() {
        RouteCreateRequest request = new RouteCreateRequest(
                train.getTrainId(),
                station.getStationId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );

        when(trainService.getTrainById(train.getTrainId())).thenReturn(train);
        when(stationService.getStationById(station.getStationId())).thenReturn(station);
        when(routeRepository.save(any(Route.class))).thenAnswer(inv -> inv.getArgument(0));

        Route saved = routeService.createRoute(request);
        assertNotNull(saved);
        assertEquals(train, saved.getTrain());
        assertEquals(station, saved.getStation());
        assertEquals(1, saved.getSequenceNum());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testUpdateRoute_RouteDoesNotExist() {
        RouteUpdateRequest request = new RouteUpdateRequest(
                train.getTrainId(),
                station.getStationId(),
                5,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        when(trainService.getTrainById(train.getTrainId())).thenReturn(train);
        when(stationService.getStationById(station.getStationId())).thenReturn(station);
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Route> result = routeService.updateRoute(999L, request);

        assertTrue(result.isEmpty());
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testUpdateRoute_RouteExists() {
        RouteUpdateRequest request = new RouteUpdateRequest(
                train.getTrainId(),
                station.getStationId(),
                5,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );

        when(trainService.getTrainById(train.getTrainId())).thenReturn(train);
        when(stationService.getStationById(station.getStationId())).thenReturn(station);
        when(routeRepository.findById(route.getRouteId())).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Route> result = routeService.updateRoute(route.getRouteId(), request);

        assertTrue(result.isPresent());
        Route updated = result.get();
        assertEquals(5, updated.getSequenceNum());
        assertEquals(train, updated.getTrain());
        assertEquals(station, updated.getStation());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testDeleteRoute_RouteDoesNotExist() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());
        boolean deleted = routeService.deleteRoute(1L);
        assertFalse(deleted);
        verify(routeRepository, never()).deleteById(1L);
    }

    @Test
    void testDeleteRoute_RouteExist() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        doNothing().when(routeRepository).deleteById(1L);
        boolean deleted = routeService.deleteRoute(1L);
        assertTrue(deleted);
        verify(routeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetRoutesByStationId() {
        when(routeRepository.findByStationStationId(station.getStationId()))
                .thenReturn(List.of(route));

        List<Route> routes = routeService.getRoutesByStationId(station.getStationId());

        assertEquals(1, routes.size());
        assertEquals(route.getRouteId(), routes.get(0).getRouteId());
        verify(routeRepository, times(1)).findByStationStationId(station.getStationId());
    }

    @Test
    void testGetRoutesByTrainId() {
        when(routeRepository.findByTrainTrainId(train.getTrainId()))
                .thenReturn(List.of(route));

        List<Route> routes = routeService.getRoutesByTrainId(train.getTrainId());

        assertEquals(1, routes.size());
        assertEquals(route.getRouteId(), routes.get(0).getRouteId());
        verify(routeRepository, times(1)).findByTrainTrainId(train.getTrainId());
    }
}

package com.platformone.train.service.impl;

import com.platformone.train.entity.Station;
import com.platformone.train.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class StationServiceImplTest {
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationServiceImpl stationService;

    private Station station;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        station=new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh");
    }

    @Test
    void testGetStationById_StationExists(){
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        Station found=stationService.getStationById(1L);
        assertNotNull(found);
        assertEquals("Kanpur Central",found.getName());
    }

    @Test
    void testGetStationById_StationDoesNotExist(){
        when(stationRepository.findById(1L)).thenReturn(Optional.empty());
        Station found=stationService.getStationById(1L);
        assertNull(found);
    }

    @Test
    void testCreateStation(){
        when(stationRepository.save(any(Station.class))).thenReturn(station);
        Station created=stationService.createStation(station);
        assertNotNull(created);
        assertEquals("Kanpur Central",created.getName());
    }

    @Test
    void testUpdateStation_StationDoesNotExist(){
        when(stationRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Station> result=stationService.updateStation(1L,station);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateStation_StationExists(){
        Station newStation=new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh");
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Optional<Station> result=stationService.updateStation(1L,newStation);
        assertTrue(result.isPresent());
        assertEquals("Kanpur Central",result.get().getName());
        assertEquals("CNB",result.get().getCode());
        assertEquals("Kanpur",result.get().getCity());
        assertEquals("Uttar Pradesh",result.get().getState());
    }

    @Test
    void testDeleteStation_StationDoesNotExist(){
        when(stationRepository.findById(1L)).thenReturn(Optional.empty());
        boolean deleted=stationService.deleteStation(1L);
        assertFalse(deleted);
        verify(stationRepository,never()).deleteById(1L);
    }

    @Test
    void testDeleteStation_StationExist(){
        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));
        doNothing().when(stationRepository).deleteById(1L);
        boolean deleted=stationService.deleteStation(1L);
        assertTrue(deleted);
        verify(stationRepository,times(1)).deleteById(1L);
    }
}

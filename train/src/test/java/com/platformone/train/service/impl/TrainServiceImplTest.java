package com.platformone.train.service.impl;

import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import com.platformone.train.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainServiceImplTest {
    @Mock
    private TrainRepository trainRepository;

    @InjectMocks
    private TrainServiceImpl trainService;

    private Train train;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        train=new Train("Rajdhani Express", TrainType.EXPRESS);
    }

    @Test
    void testGetTrainById_TrainExists(){
        when(trainRepository.findById(1L)).thenReturn(Optional.of(train));
        Train found=trainService.getTrainById(1L);
        assertNotNull(found);
        assertEquals("Rajdhani Express",found.getName());
    }

    @Test
    void testGetTrainById_TrainDoesNotExist(){
        when(trainRepository.findById(1L)).thenReturn(Optional.empty());
        Train found=trainService.getTrainById(1L);
        assertNull(found);
    }

    @Test
    void testCreateTrain(){
        when(trainRepository.save(any(Train.class))).thenReturn(train);
        Train created=trainService.createTrain(train);
        assertNotNull(created);
        assertEquals("Rajdhani Express",created.getName());
        assertEquals(TrainType.EXPRESS,created.getType());
    }

    @Test
    void testUpdateTrain_TrainDoesNotExist(){
        when(trainRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Train> result=trainService.updateTrain(1L,train);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateTrain_TrainExists(){
        Train newTrain=new Train("Duronto Express",TrainType.EXPRESS);
        when(trainRepository.findById(1L)).thenReturn(Optional.of(train));
        when(trainRepository.save(any(Train.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Optional<Train> result=trainService.updateTrain(1L,newTrain);
        assertTrue(result.isPresent());
        assertEquals("Duronto Express",result.get().getName());
        assertEquals(TrainType.EXPRESS,result.get().getType());
    }

    @Test
    void testDeleteTrain_TrainDoesNotExist(){
        when(trainRepository.findById(1L)).thenReturn(Optional.empty());
        boolean deleted=trainService.deleteTrain(1L);
        assertFalse(deleted);
        verify(trainRepository,never()).deleteById(1L);
    }

    @Test
    void testDeleteTrain_TrainExist(){
        when(trainRepository.findById(1L)).thenReturn(Optional.of(train));
        doNothing().when(trainRepository).deleteById(1L);
        boolean deleted=trainService.deleteTrain(1L);
        assertTrue(deleted);
        verify(trainRepository,times(1)).deleteById(1L);
    }

    @Test
    void testGetTrainBySrcDest() {
        when(trainRepository.findTrainsBySrcAndDest("HWH", "NDLS"))
                .thenReturn(List.of(train));

        List<Train> result = trainService.getTrainBySrcDest("HWH", "NDLS");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rajdhani Express");

        verify(trainRepository, times(1)).findTrainsBySrcAndDest("HWH", "NDLS");
    }
}

package com.platformone.schedule.service.impl;

import com.platformone.schedule.clients.TrainClient;
import com.platformone.schedule.dto.ScheduleSearchResponseDTO;
import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.external.Route;
import com.platformone.schedule.external.Station;
import com.platformone.schedule.external.Train;
import com.platformone.schedule.external.TrainType;
import com.platformone.schedule.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ScheduleServiceImplTest {
    private ScheduleRepository scheduleRepository;
    private TrainClient trainClient;
    private ScheduleServiceImpl scheduleService;

    @BeforeEach
    void setup() {
        scheduleRepository = mock(ScheduleRepository.class);
        trainClient = mock(TrainClient.class);
        scheduleService = new ScheduleServiceImpl(scheduleRepository, trainClient);
    }

    @Test
    void getScheduleById_success() {
        Schedule schedule = new Schedule();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleService.getScheduleById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getScheduleId()).isEqualTo(0L);
        verify(scheduleRepository).findById(1L);
    }

    @Test
    void getScheduleById_notFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        Schedule result = scheduleService.getScheduleById(99L);

        assertThat(result).isNull();
        verify(scheduleRepository).findById(99L);
    }

    @Test
    void updateSchedule_success() {
        Schedule existing = new Schedule();
        existing.setAvailableSeats(100);

        Schedule update = new Schedule();
        update.setAvailableSeats(90);
        update.setTotalSeats(100);
        update.setTrainId(2L);
        update.setScheduleDate(LocalDate.now());

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(scheduleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Schedule> result = scheduleService.updateSchedule(1L, update);

        assertThat(result).isPresent();
        assertThat(result.get().getAvailableSeats()).isEqualTo(90);
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    void updateSchedule_notFound() {
        when(scheduleRepository.findById(404L)).thenReturn(Optional.empty());

        Optional<Schedule> result = scheduleService.updateSchedule(404L, new Schedule());

        assertThat(result).isEmpty();
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void deleteSchedule_success() {
        Schedule s = new Schedule();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(s));

        boolean deleted = scheduleService.deleteSchedule(1L);

        assertThat(deleted).isTrue();
        verify(scheduleRepository).deleteById(1L);
    }

    @Test
    void deleteSchedule_notFound() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = scheduleService.deleteSchedule(99L);

        assertThat(deleted).isFalse();
        verify(scheduleRepository, never()).deleteById(anyLong());
    }

    @Test
    void decrementAvailableSeats_success() {
        Schedule s = new Schedule();
        s.setAvailableSeats(2);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(s));
        when(scheduleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Schedule> result = scheduleService.decrementAvailableSeats(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getAvailableSeats()).isEqualTo(1);
        verify(scheduleRepository).save(any());
    }

    @Test
    void decrementAvailableSeats_noSeats() {
        Schedule s = new Schedule();
        s.setAvailableSeats(0);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(s));

        Optional<Schedule> result = scheduleService.decrementAvailableSeats(1L);

        assertThat(result).isEmpty();
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void findSchedulesBySrcDestDate_success() {
        Train train = new Train("Rajdhani Express", TrainType.EXPRESS);
        train.setTrainId(1L);

        Station srcStation = new Station("Source Station", "SRC", "City1", "State1");
        Station destStation = new Station("Dest Station", "DST", "City2", "State2");

        Route srcRoute = new Route(1, null, LocalDateTime.of(2025, 10, 6, 10, 0));
        srcRoute.setStation(srcStation);

        Route destRoute = new Route(2, LocalDateTime.of(2025, 10, 6, 14, 0), null);
        destRoute.setStation(destStation);

        train.setRoutes(List.of(srcRoute, destRoute));

        Schedule schedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 100, 90);

        when(trainClient.getTrainBySrcDest("SRC", "DST")).thenReturn(List.of(train));
        when(scheduleRepository.findByTrainIdAndJourneyDate(List.of(1L), LocalDate.of(2025, 10, 6)))
                .thenReturn(List.of(schedule));

        List<ScheduleSearchResponseDTO> result =
                scheduleService.findSchedulesBySrcDestDate("SRC", "DST", LocalDate.of(2025, 10, 6));

        assertThat(result).hasSize(1);
        ScheduleSearchResponseDTO dto = result.getFirst();

        assertThat(dto.trainName()).isEqualTo("Rajdhani Express");
        assertThat(dto.sourceStationCode()).isEqualTo("SRC");
        assertThat(dto.destinationStationCode()).isEqualTo("DST");
        assertThat(dto.availableSeats()).isEqualTo(90);
    }

    @Test
    void findSchedulesBySrcDestDate_noTrains() {
        when(trainClient.getTrainBySrcDest("SRC", "DST")).thenReturn(List.of());

        List<ScheduleSearchResponseDTO> result =
                scheduleService.findSchedulesBySrcDestDate("SRC", "DST", LocalDate.of(2025, 10, 6));

        assertThat(result).isEmpty();
        verify(scheduleRepository, never()).findByTrainIdAndJourneyDate(any(), any());
    }
}
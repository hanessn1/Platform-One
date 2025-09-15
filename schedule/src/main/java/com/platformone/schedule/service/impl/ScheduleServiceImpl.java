package com.platformone.schedule.service.impl;

import com.platformone.schedule.clients.TrainClient;
import com.platformone.schedule.dto.ScheduleSearchResponseDTO;
import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.external.Route;
import com.platformone.schedule.external.Train;
import com.platformone.schedule.repository.ScheduleRepository;
import com.platformone.schedule.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TrainClient trainClient;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, TrainClient trainClient1) {
        this.scheduleRepository = scheduleRepository;
        this.trainClient = trainClient1;
    }

    @Override
    public Schedule getScheduleById(long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> updateSchedule(long scheduleId, Schedule updatedSchedule) {
        return scheduleRepository.findById(scheduleId).map(schedule -> {
            schedule.setAvailableSeats(updatedSchedule.getAvailableSeats());
            schedule.setScheduleDate(updatedSchedule.getScheduleDate());
            schedule.setTotalSeats(updatedSchedule.getTotalSeats());
            schedule.setTrainId(updatedSchedule.getTrainId());
            return scheduleRepository.save(schedule);
        });
    }

    @Override
    public boolean deleteSchedule(long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        if (schedule == null) return false;
        scheduleRepository.deleteById(scheduleId);
        return true;
    }

    @Override
    public List<ScheduleSearchResponseDTO> findSchedulesBySrcDestDate(String src, String dest, LocalDate journeyDate) {
        List<Train> trains = trainClient.getTrainBySrcDest(src, dest);
        if (trains.isEmpty()) return List.of();

        List<Long> trainIds = trains.stream().map(train -> {
            return train.getTrainId();
        }).toList();

        List<Schedule> schedules = scheduleRepository.findByTrainIdAndJourneyDate(trainIds, journeyDate);
        if (schedules.isEmpty()) return List.of();

        return schedules.stream().map(schedule -> {
            Train train = trains.stream().filter(train1 -> {
                return train1.getTrainId() == schedule.getTrainId();
            }).findFirst().orElseThrow();

            return convertToScheduleSearchResponseDTO(schedule, train, src, dest);
        }).toList();
    }

    private ScheduleSearchResponseDTO convertToScheduleSearchResponseDTO(Schedule schedule, Train train, String src, String dest) {
        Route srcRoute = train.getRoutes().stream().filter(route -> {
            return route.getStation().getCode().equalsIgnoreCase(src);
        }).findFirst().orElseThrow();

        Route destRoute = train.getRoutes().stream().filter(route -> {
            return route.getStation().getCode().equalsIgnoreCase(dest);
        }).findFirst().orElseThrow();

        return new ScheduleSearchResponseDTO(
                schedule.getScheduleId(),
                train.getTrainId(),
                train.getName(),
                train.getType(),
                src,
                srcRoute.getStation().getName(),
                srcRoute.getDepartureTime(),
                dest,
                destRoute.getStation().getName(),
                destRoute.getArrivalTime(),
                schedule.getScheduleDate(),
                schedule.getTotalSeats(),
                schedule.getAvailableSeats(),
                calculateDuration(srcRoute.getDepartureTime(), destRoute.getArrivalTime()),
                calculateDayOffset(srcRoute.getDepartureTime(), destRoute.getArrivalTime())
        );
    }

    private int calculateDayOffset(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return (int) ChronoUnit.DAYS.between(departureTime.toLocalDate(), arrivalTime.toLocalDate());
    }

    private String calculateDuration(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Duration duration = Duration.between(departureTime, arrivalTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(minutes == 1 ? " min " : " mins");
        }
        return sb.toString().trim();
    }
}

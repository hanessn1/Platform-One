package com.platformone.schedule.service.impl;

import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.repository.ScheduleRepository;
import com.platformone.schedule.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
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
            schedule.setJourneyDate(updatedSchedule.getJourneyDate());
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
}

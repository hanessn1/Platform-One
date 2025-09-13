package com.platformone.schedule.service;

import com.platformone.schedule.entity.Schedule;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ScheduleService {

    Schedule getScheduleById(long scheduleId);

    Schedule createSchedule(Schedule schedule);

    Optional<Schedule> updateSchedule(long scheduleId, Schedule updatedSchedule);

    boolean deleteSchedule(long scheduleId);
}

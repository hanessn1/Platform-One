package com.platformone.schedule.service;

import com.platformone.schedule.dto.ScheduleSearchResponseDTO;
import com.platformone.schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    Schedule getScheduleById(long scheduleId);

    Schedule createSchedule(Schedule schedule);

    Optional<Schedule> updateSchedule(long scheduleId, Schedule updatedSchedule);

    boolean deleteSchedule(long scheduleId);

    List<ScheduleSearchResponseDTO> findSchedulesBySrcDestDate(String src, String dest, LocalDate localDate);

    Optional<Schedule> decrementAvailableSeats(long scheduleId);
}

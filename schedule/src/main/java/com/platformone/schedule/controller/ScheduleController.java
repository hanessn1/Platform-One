package com.platformone.schedule.controller;

import com.platformone.schedule.dto.ScheduleSearchResponseDTO;
import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable long scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        if (schedule == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule savedSchedule = scheduleService.createSchedule(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable long scheduleId, @RequestBody Schedule updatedSchedule) {
        Optional<Schedule> schedule = scheduleService.updateSchedule(scheduleId, updatedSchedule);
        if (schedule.isPresent())
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSchedule(@PathVariable long scheduleId) {
        boolean deleted = scheduleService.deleteSchedule(scheduleId);
        if (deleted)
            return new ResponseEntity<>("Schedule deleted successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Schedule not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ScheduleSearchResponseDTO>> findSchedulesBySrcDestDate(
            @RequestParam String src,
            @RequestParam String dest,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        return new ResponseEntity<>(
                scheduleService.findSchedulesBySrcDestDate(src, dest, journeyDate),
                HttpStatus.OK
        );
    }

    @PutMapping("/{scheduleId}/decrement")
    public ResponseEntity<Schedule> decrementAvailableSeats(@PathVariable long scheduleId) {
        Optional<Schedule> schedule = scheduleService.decrementAvailableSeats(scheduleId);
        if (schedule.isPresent())
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{scheduleId}/increment")
    public ResponseEntity<Schedule> incrementAvailableSeats(@PathVariable long scheduleId) {
        Optional<Schedule> schedule = scheduleService.incrementAvailableSeats(scheduleId);
        if (schedule.isPresent())
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
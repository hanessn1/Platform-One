package com.platformone.schedule.controller;

import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule savedSchedule=scheduleService.createSchedule(schedule);
        return new ResponseEntity<>(savedSchedule,HttpStatus.CREATED);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable long scheduleId, @RequestBody Schedule updatedSchedule) {
        Optional<Schedule> schedule = scheduleService.updateSchedule(scheduleId, updatedSchedule);
        if (schedule.isPresent())
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable long scheduleId) {
        boolean deleted = scheduleService.deleteSchedule(scheduleId);
        if (deleted)
            return new ResponseEntity<>("Schedule deleted successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Schedule not found", HttpStatus.NOT_FOUND);
    }
}

package com.platformone.booking.clients;

import com.platformone.booking.external.Schedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "schedule", url = "${schedule.service.url}")
public interface ScheduleClient {
    @GetMapping("/schedule/{scheduleId}")
    Schedule getScheduleById(@PathVariable long scheduleId);

    @PutMapping("/schedule/{scheduleId}/decrement")
    public ResponseEntity<Schedule> decrementAvailableSeats(@PathVariable long scheduleId);

    @PutMapping("/schedule/{scheduleId}/increment")
    public ResponseEntity<Schedule> incrementAvailableSeats(@PathVariable long scheduleId);
}
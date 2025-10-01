package com.platformone.booking.clients;

import com.platformone.booking.external.Schedule;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "schedule", url = "${schedule.service.url}")
public interface ScheduleClient {
    @GetMapping("/schedule/{scheduleId}")
    Schedule getScheduleById(@PathVariable long scheduleId);
}
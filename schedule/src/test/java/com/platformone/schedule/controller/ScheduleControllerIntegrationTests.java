package com.platformone.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.platformone.schedule.dto.ScheduleSearchResponseDTO;
import com.platformone.schedule.entity.Schedule;
import com.platformone.schedule.external.TrainType;
import com.platformone.schedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class ScheduleControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleService scheduleService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void getScheduleById_found() throws Exception {
        Schedule schedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 100, 90);

        when(scheduleService.getScheduleById(123L)).thenReturn(schedule);

        mockMvc.perform(get("/schedule/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainId").value(1L));
    }

    @Test
    void getScheduleById_notFound() throws Exception {
        when(scheduleService.getScheduleById(999L)).thenReturn(null);

        mockMvc.perform(get("/schedule/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSchedule_success() throws Exception {
        Schedule schedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 100, 90);

        when(scheduleService.createSchedule(any(Schedule.class))).thenReturn(schedule);

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schedule)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainId").value(1L));
    }

    @Test
    void updateSchedule_found() throws Exception {
        Schedule updatedSchedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 120, 110);

        when(scheduleService.updateSchedule(eq(123L), any(Schedule.class)))
                .thenReturn(Optional.of(updatedSchedule));

        mockMvc.perform(put("/schedule/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSchedule)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(110));
    }

    @Test
    void updateSchedule_notFound() throws Exception {
        Schedule updatedSchedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 120, 110);

        when(scheduleService.updateSchedule(eq(999L), any(Schedule.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/schedule/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSchedule)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchedule_success() throws Exception {
        when(scheduleService.deleteSchedule(123L)).thenReturn(true);

        mockMvc.perform(delete("/schedule/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Schedule deleted successfully"));
    }

    @Test
    void deleteSchedule_notFound() throws Exception {
        when(scheduleService.deleteSchedule(999L)).thenReturn(false);

        mockMvc.perform(delete("/schedule/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Schedule not found"));
    }

    @Test
    void findSchedulesBySrcDestDate_success() throws Exception {
        ScheduleSearchResponseDTO dto = new ScheduleSearchResponseDTO(
                123L, 1L, "Rajdhani Express", TrainType.EXPRESS,
                "SRC", "Source Station", LocalDateTime.of(2025, 10, 6, 10, 0),
                "DST", "Destination Station", LocalDateTime.of(2025, 10, 6, 14, 0),
                LocalDate.of(2025, 10, 6), 100, 90, "4 hours", 0
        );

        when(scheduleService.findSchedulesBySrcDestDate("SRC", "DST", LocalDate.of(2025, 10, 6)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/schedule/search")
                        .param("src", "SRC")
                        .param("dest", "DST")
                        .param("journeyDate", "2025-10-06"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainName").value("Rajdhani Express"))
                .andExpect(jsonPath("$[0].sourceStationCode").value("SRC"))
                .andExpect(jsonPath("$[0].destinationStationCode").value("DST"));
    }

    @Test
    void decrementAvailableSeats_success() throws Exception {
        Schedule schedule = new Schedule(1L, LocalDate.of(2025, 10, 6), 100, 89);

        when(scheduleService.decrementAvailableSeats(123L)).thenReturn(Optional.of(schedule));

        mockMvc.perform(put("/schedule/123/decrement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableSeats").value(89));
    }

    @Test
    void decrementAvailableSeats_failure() throws Exception {
        when(scheduleService.decrementAvailableSeats(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/schedule/999/decrement"))
                .andExpect(status().isBadRequest());
    }
}

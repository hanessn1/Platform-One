package com.platformone.schedule.dto;

import com.platformone.schedule.external.TrainType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleSearchResponseDTO(
        long scheduleId,
        long trainId,
        String trainName,
        TrainType trainType,
        String sourceStationCode,
        String sourceStationName,
        LocalDateTime departureTime,

        String destinationStationCode,
        String destinationStationName,
        LocalDateTime arrivalTime,

        LocalDate journeyDate,
        int TotalSeats,
        int availableSeats,
        double fareAmount,
        String duration,
        int dayOffset
) {
}
package com.platformone.schedule.repository;

import com.platformone.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.trainId IN :trainIds " +
            "AND s.scheduleDate=:journeyDate")
    List<Schedule> findByTrainIdAndJourneyDate(
            @Param("trainIds") List<Long> trainIds,
            @Param("journeyDate") LocalDate journeyDate
    );
}

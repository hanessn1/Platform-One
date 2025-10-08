package com.platformone.schedule.repository;

import com.platformone.schedule.entity.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ScheduleRepositoryTest {
    @Autowired
    private ScheduleRepository scheduleRepository;

    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule schedule3;

    private static final LocalDate TEST_DATE = LocalDate.of(2025, 10, 6);

    @BeforeEach
    void setup() {
        scheduleRepository.deleteAll();

        schedule1 = new Schedule();
        schedule1.setTrainId(100L);
        schedule1.setScheduleDate(TEST_DATE);
        schedule1.setTotalSeats(100);
        schedule1.setAvailableSeats(80);

        schedule2 = new Schedule();
        schedule2.setTrainId(200L);
        schedule2.setScheduleDate(TEST_DATE);
        schedule2.setTotalSeats(120);
        schedule2.setAvailableSeats(110);

        schedule3 = new Schedule();
        schedule3.setTrainId(300L);
        schedule3.setScheduleDate(TEST_DATE);
        schedule3.setTotalSeats(90);
        schedule3.setAvailableSeats(70);

        scheduleRepository.saveAll(List.of(schedule1, schedule2, schedule3));
        scheduleRepository.flush();
    }

    @Test
    void testFindByTrainIdAndJourneyDate() {
        List<Schedule> found = scheduleRepository.findByTrainIdAndJourneyDate(
                List.of(100L, 200L),
                LocalDate.of(2025, 10, 6)
        );

        assertThat(found).hasSize(2);
        assertThat(found)
                .extracting(Schedule::getTrainId)
                .containsExactlyInAnyOrder(100L, 200L);
    }

    @Test
    void testFindById() {
        Optional<Schedule> found = scheduleRepository.findById(schedule1.getScheduleId());
        assertThat(found).isPresent();
        assertThat(found.get().getTrainId()).isEqualTo(100L);
        assertThat(found.get().getAvailableSeats()).isEqualTo(80);
    }

    @Test
    void testSaveAnotherSchedule() {
        Schedule newSchedule = new Schedule();
        newSchedule.setTrainId(400L);
        newSchedule.setScheduleDate(LocalDate.of(2025, 10, 8));
        newSchedule.setTotalSeats(200);
        newSchedule.setAvailableSeats(190);

        Schedule saved = scheduleRepository.save(newSchedule);

        assertThat(saved.getScheduleId()).isNotNull();
        assertThat(saved.getTrainId()).isEqualTo(400L);
        assertThat(saved.getTotalSeats()).isEqualTo(200);

        List<Schedule> all = scheduleRepository.findAll();
        assertThat(all).hasSize(4);
    }

    @Test
    void testDeleteSchedule() {
        Long idToDelete = schedule2.getScheduleId();

        scheduleRepository.deleteById(idToDelete);

        Optional<Schedule> deleted = scheduleRepository.findById(idToDelete);
        assertThat(deleted).isEmpty();

        List<Schedule> remaining = scheduleRepository.findAll();
        assertThat(remaining).hasSize(2);
    }
}
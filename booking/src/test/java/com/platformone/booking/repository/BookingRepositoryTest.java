package com.platformone.booking.repository;

import com.platformone.booking.entities.Booking;
import com.platformone.booking.entities.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking(
                1L,
                101L,
                BookingStatus.CONFIRMED,
                5,
                "PNR12345",
                550.0
        );
        booking = bookingRepository.save(booking);
    }

    @Test
    void testFindById() {
        Optional<Booking> found = bookingRepository.findById(booking.getBookingId());

        assertThat(found).isPresent();
        assertThat(found.get().getPnr()).isEqualTo("PNR12345");
        assertThat(found.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void testSaveAnotherBooking() {
        Booking anotherBooking = new Booking(
                2L,
                102L,
                BookingStatus.WAITINGLIST,
                8,
                "PNR67890",
                750.0
        );

        Booking saved = bookingRepository.save(anotherBooking);
        assertThat(saved.getBookingId()).isGreaterThan(0);
        assertThat(saved.getPnr()).isEqualTo("PNR67890");
        long count = bookingRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testDeleteBooking() {
        bookingRepository.deleteById(booking.getBookingId());

        Optional<Booking> deleted = bookingRepository.findById(booking.getBookingId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindByPnr() {
        Optional<Booking> found = bookingRepository.findByPnr("PNR12345");
        assertThat(found).isPresent();
        assertThat(found.get().getSeatNumber()).isEqualTo(5);
    }
}
package com.platformone.booking.kafka;

import com.platformone.booking.clients.ScheduleClient;
import com.platformone.booking.consumer.PaymentEventConsumer;
import com.platformone.booking.entities.Booking;
import com.platformone.booking.entities.BookingStatus;
import com.platformone.booking.events.PaymentFailedEvent;
import com.platformone.booking.events.PaymentSucceededEvent;
import com.platformone.booking.exception.BookingNotFoundException;
import com.platformone.booking.repository.BookingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentEventConsumerTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ScheduleClient scheduleClient;

    @InjectMocks
    private PaymentEventConsumer consumer;

    private Booking booking;
    private PaymentSucceededEvent succeededEvent;
    private PaymentFailedEvent failedEvent;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        long userId = 1L;
        long bookingId = 2L;
        long scheduleId = 10L;
        booking = new Booking(userId, scheduleId, BookingStatus.PROCESSING, 20, "PNR");

        succeededEvent = new PaymentSucceededEvent(bookingId, 100L);
        failedEvent = new PaymentFailedEvent(bookingId, 101L, "Insufficient balance");
    }

    @Test
    void handlePaymentSuccess_updatesBookingStatusToConfirmed() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        consumer.handlePaymentSuccess(succeededEvent);

        Assertions.assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void handlePaymentFailure_updatesBookingStatusToFailedAndIncrementsSeats() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        consumer.handlePaymentFailure(failedEvent);

        Assertions.assertEquals(BookingStatus.FAILED, booking.getBookingStatus());
        verify(bookingRepository).save(booking);
        verify(scheduleClient).incrementAvailableSeats(booking.getScheduleId());
    }

    @Test
    void handlePaymentSuccess_bookingNotFound_throwsException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class,
                () -> consumer.handlePaymentSuccess(succeededEvent));
    }

    @Test
    void handlePaymentFailure_bookingNotFound_throwsException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class,
                () -> consumer.handlePaymentFailure(failedEvent));
    }
}
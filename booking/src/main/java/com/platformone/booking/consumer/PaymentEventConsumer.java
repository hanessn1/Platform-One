package com.platformone.booking.consumer;

import com.platformone.booking.clients.ScheduleClient;
import com.platformone.booking.entities.Booking;
import com.platformone.booking.entities.BookingStatus;
import com.platformone.booking.events.PaymentFailedEvent;
import com.platformone.booking.events.PaymentSucceededEvent;
import com.platformone.booking.exception.BookingNotFoundException;
import com.platformone.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final BookingRepository bookingRepository;
    private final ScheduleClient scheduleClient;

    public PaymentEventConsumer(BookingRepository bookingRepository, ScheduleClient scheduleClient) {
        this.bookingRepository = bookingRepository;
        this.scheduleClient = scheduleClient;
    }

    @KafkaListener(
            topics = "payment_succeeded",
            groupId = "booking-service",
            containerFactory = "paymentSucceededKafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(PaymentSucceededEvent event) {
        log.debug("Received PaymentSucceededEvent: {}", event);
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @KafkaListener(
            topics = "payment_failed",
            groupId = "booking-service",
            containerFactory = "paymentFailedKafkaListenerContainerFactory"
    )
    public void handlePaymentFailure(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent: {}", event);
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        booking.setBookingStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);
        scheduleClient.incrementAvailableSeats(booking.getScheduleId());
    }
}
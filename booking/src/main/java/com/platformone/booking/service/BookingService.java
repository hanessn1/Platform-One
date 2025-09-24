package com.platformone.booking.service;

import com.platformone.booking.entities.Booking;

import java.util.Optional;

public interface BookingService {
    Booking getBookingById(long bookingId);

    Booking createBooking(Booking newBooking);

    Optional<Booking> updateBooking(long bookingId, Booking updatedBooking);

    boolean deleteBooking(long bookingId);
}

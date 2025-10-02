package com.platformone.booking.service;

import com.platformone.booking.dto.BookingRequestDTO;
import com.platformone.booking.dto.BookingResponseDTO;
import com.platformone.booking.entities.Booking;

import java.util.Optional;

public interface BookingService {
    Booking getBookingById(long bookingId);

    BookingResponseDTO createBooking(BookingRequestDTO newBooking);

    Optional<Booking> updateBooking(long bookingId, Booking updatedBooking);

    boolean deleteBooking(long bookingId);

    BookingResponseDTO getBookingByPnr(String pnr);
}

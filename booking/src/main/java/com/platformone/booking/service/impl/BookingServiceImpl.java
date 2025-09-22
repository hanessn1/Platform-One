package com.platformone.booking.service.impl;

import com.platformone.booking.entities.Booking;
import com.platformone.booking.repository.BookingRepository;
import com.platformone.booking.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }


    @Override
    public Booking getBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public Booking createBooking(Booking newBooking) {
        return bookingRepository.save(newBooking);
    }

    @Override
    public Optional<Booking> updateBooking(long bookingId, Booking updatedBooking) {
        return bookingRepository.findById(bookingId).map(booking -> {
            booking.setBookingStatus(updatedBooking.getBookingStatus());
            booking.setPnr(updatedBooking.getPnr());
            booking.setFareAmount(updatedBooking.getFareAmount());
            booking.setScheduleId(updatedBooking.getScheduleId());
            booking.setSeatNumber(updatedBooking.getSeatNumber());
            booking.setUserId(updatedBooking.getUserId());
            return bookingRepository.save(booking);
        });
    }

    @Override
    public boolean deleteBooking(long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) return false;
        bookingRepository.deleteById(bookingId);
        return true;
    }
}

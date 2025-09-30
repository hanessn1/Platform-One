package com.platformone.booking.controller;

import com.platformone.booking.entities.Booking;
import com.platformone.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking newBooking) {
        Booking savedBooking = bookingService.createBooking(newBooking);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable long bookingId, @RequestBody Booking updatedBooking) {
        Optional<Booking> booking = bookingService.updateBooking(bookingId, updatedBooking);
        if (booking.isPresent())
            return new ResponseEntity<>(booking.get(), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable long bookingId) {
        boolean deleted = bookingService.deleteBooking(bookingId);
        if (deleted)
            return new ResponseEntity<>("Booking deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>("Booking not found", HttpStatus.NOT_FOUND);
    }
}

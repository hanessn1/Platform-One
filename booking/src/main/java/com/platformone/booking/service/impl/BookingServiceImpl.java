package com.platformone.booking.service.impl;

import com.platformone.booking.clients.ScheduleClient;
import com.platformone.booking.clients.TrainClient;
import com.platformone.booking.dto.BookingResponseDTO;
import com.platformone.booking.entities.Booking;
import com.platformone.booking.external.Route;
import com.platformone.booking.external.Schedule;
import com.platformone.booking.external.Train;
import com.platformone.booking.repository.BookingRepository;
import com.platformone.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final ScheduleClient scheduleClient;
    private final TrainClient trainClient;

    public BookingServiceImpl(BookingRepository bookingRepository, ScheduleClient scheduleClient, TrainClient trainClient) {
        this.bookingRepository = bookingRepository;
        this.scheduleClient = scheduleClient;
        this.trainClient = trainClient;
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

    @Override
    public BookingResponseDTO getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr).orElse(null);
        if (booking == null)
            return null;

        Schedule schedule = scheduleClient.getScheduleById(booking.getScheduleId());
        if(log.isDebugEnabled()){
            log.debug("Fetched schedule: {}", schedule);
        }

        Train train = trainClient.getTrainById(schedule.getTrainId());
        Route sourceRoute = train.getRoutes().getFirst();
        Route destinationRoute = train.getRoutes().getLast();

        BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setPnr(booking.getPnr());
        bookingResponseDTO.setBookingStatus(booking.getBookingStatus());
        bookingResponseDTO.setSeatNumber(booking.getSeatNumber());
        bookingResponseDTO.setFareAmount(booking.getFareAmount());
        bookingResponseDTO.setBookingDate(LocalDate.ofInstant(booking.getCreatedAt(),ZoneId.systemDefault()));

        bookingResponseDTO.setTrainName(train.getName());
        bookingResponseDTO.setTrainType(train.getType());

        bookingResponseDTO.setSourceStationCode(sourceRoute.getStation().getCode());
        bookingResponseDTO.setSourceStationName(sourceRoute.getStation().getName());
        bookingResponseDTO.setDepartureTime(sourceRoute.getDepartureTime());

        bookingResponseDTO.setDestinationStationCode(destinationRoute.getStation().getCode());
        bookingResponseDTO.setDestinationStationName(destinationRoute.getStation().getName());
        bookingResponseDTO.setArrivalTime(destinationRoute.getArrivalTime());

        bookingResponseDTO.setJourneyDate(schedule.getScheduleDate());
        return bookingResponseDTO;
    }
}

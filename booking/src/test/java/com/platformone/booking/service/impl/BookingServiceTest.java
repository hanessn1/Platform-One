package com.platformone.booking.service.impl;

import com.platformone.booking.clients.ScheduleClient;
import com.platformone.booking.clients.TrainClient;
import com.platformone.booking.dto.BookingRequestDTO;
import com.platformone.booking.dto.BookingResponseDTO;
import com.platformone.booking.entities.Booking;
import com.platformone.booking.entities.BookingStatus;
import com.platformone.booking.exception.ScheduleNotFoundException;
import com.platformone.booking.external.*;
import com.platformone.booking.repository.BookingRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ScheduleClient scheduleClient;

    @Mock
    private TrainClient trainClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingRequestDTO bookingRequestDTO;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booking = new Booking(1L, 101L, BookingStatus.CONFIRMED, 3, "PNR12345");
        bookingRequestDTO = new BookingRequestDTO(1L, 101L, 600.0);

        schedule = mock(Schedule.class);
        when(schedule.getTrainId()).thenReturn(1L);
        when(schedule.getScheduleDate()).thenReturn(LocalDate.now());
        when(schedule.getAvailableSeats()).thenReturn(5);
        when(scheduleClient.getScheduleById(anyLong())).thenReturn(schedule);

        Route sourceRoute = mock(Route.class);
        Station sourceStation = mock(Station.class);
        when(sourceStation.getCode()).thenReturn("SRC");
        when(sourceStation.getName()).thenReturn("Source Station");
        when(sourceRoute.getStation()).thenReturn(sourceStation);
        when(sourceRoute.getDepartureTime()).thenReturn(LocalDateTime.now());

        Route destinationRoute = mock(Route.class);
        Station destinationStation = mock(Station.class);
        when(destinationStation.getCode()).thenReturn("DST");
        when(destinationStation.getName()).thenReturn("Destination Station");
        when(destinationRoute.getStation()).thenReturn(destinationStation);
        when(destinationRoute.getArrivalTime()).thenReturn(LocalDateTime.now().plusHours(2));

        List<Route> routes = new ArrayList<>();
        routes.add(sourceRoute);
        routes.add(destinationRoute);

        Train train = mock(Train.class);
        when(train.getRoutes()).thenReturn(routes);
        when(train.getName()).thenReturn("Express");
        when(train.getType()).thenReturn(TrainType.EXPRESS);

        when(trainClient.getTrainById(anyLong())).thenReturn(train);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);

            Field createdField = Booking.class.getDeclaredField("createdAt");
            createdField.setAccessible(true);
            createdField.set(saved, Instant.now());

            Field updatedField = Booking.class.getDeclaredField("updatedAt");
            updatedField.setAccessible(true);
            updatedField.set(saved, Instant.now());

            return saved;
        });
        when(bookingRepository.findByPnr(anyString())).thenReturn(Optional.of(booking));
    }

    @Test
    void testGetBookingById_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPnr()).isEqualTo("PNR12345");
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookingById_NotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        Booking result = bookingService.getBookingById(99L);

        assertThat(result).isNull();
    }

    @Test
    void testCreateBooking_WhenSeatsAvailable() {
        BookingResponseDTO response = bookingService.createBooking(bookingRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getBookingStatus()).isEqualTo(BookingStatus.PROCESSING);

        verify(bookingRepository).save(any(Booking.class));
        verify(scheduleClient).decrementAvailableSeats(anyLong());
    }

    @Test
    void testCreateBooking_WhenNoSeatsAvailable() {
        when(schedule.getAvailableSeats()).thenReturn(0);

        BookingRequestDTO newBooking = new BookingRequestDTO();
        newBooking.setUserId(1L);
        newBooking.setScheduleId(101L);

        BookingResponseDTO response = bookingService.createBooking(newBooking);

        assertThat(response).isNotNull();
        assertThat(response.getBookingStatus()).isEqualTo(BookingStatus.WAITINGLIST);
        verify(scheduleClient, never()).decrementAvailableSeats(anyLong());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WhenScheduleNotFound() {
        BookingRequestDTO newBooking = new BookingRequestDTO();
        newBooking.setUserId(1L);
        newBooking.setScheduleId(202L);

        FeignException.NotFound notFoundException = new FeignException.NotFound(
                "Schedule not found",
                Request.create(Request.HttpMethod.GET, "/schedules/202", Map.of(), null, new RequestTemplate()),
                null,
                null
        );

        when(scheduleClient.getScheduleById(202L)).thenThrow(notFoundException);

        assertThatThrownBy(() -> bookingService.createBooking(newBooking))
                .isInstanceOf(ScheduleNotFoundException.class)
                .hasMessageContaining("Schedule not found with id: 202");
    }

    @Test
    void testUpdateBooking_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updatedBooking = new Booking(2L, 202L, BookingStatus.CANCELLED, 7, "NEWPNR");

        Optional<Booking> result = bookingService.updateBooking(1L, updatedBooking);

        assertThat(result).isPresent();
        assertThat(result.get().getBookingStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(result.get().getPnr()).isEqualTo("NEWPNR");
    }

    @Test
    void testUpdateBooking_NotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Booking> result = bookingService.updateBooking(99L, booking);

        assertThat(result).isEmpty();
    }

    @Test
    void testDeleteBooking_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        boolean result = bookingService.deleteBooking(1L);

        assertThat(result).isTrue();
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void testDeleteBooking_NotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = bookingService.deleteBooking(99L);

        assertThat(result).isFalse();
        verify(bookingRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetBookingByPnr_Found() throws Exception {
        Field createdField = Booking.class.getDeclaredField("createdAt");
        createdField.setAccessible(true);
        createdField.set(booking, Instant.now());

        Field updatedField = Booking.class.getDeclaredField("updatedAt");
        updatedField.setAccessible(true);
        updatedField.set(booking, Instant.now());

        when(bookingRepository.findByPnr("PNR12345")).thenReturn(Optional.of(booking));

        BookingResponseDTO result = bookingService.getBookingByPnr("PNR12345");

        assertThat(result).isNotNull();
        assertThat(result.getPnr()).isEqualTo("PNR12345");
        verify(bookingRepository).findByPnr("PNR12345");
    }

    @Test
    void testGetBookingByPnr_NotFound() {
        when(bookingRepository.findByPnr("INVALID")).thenReturn(Optional.empty());

        BookingResponseDTO result = bookingService.getBookingByPnr("INVALID");

        assertThat(result).isNull();
    }
}
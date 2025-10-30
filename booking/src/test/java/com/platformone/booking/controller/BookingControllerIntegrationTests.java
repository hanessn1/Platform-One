package com.platformone.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.booking.config.SecurityConfig;
import com.platformone.booking.dto.BookingRequestDTO;
import com.platformone.booking.dto.BookingResponseDTO;
import com.platformone.booking.entities.Booking;
import com.platformone.booking.entities.BookingStatus;
import com.platformone.booking.jwt.CustomAccessDeniedHandler;
import com.platformone.booking.jwt.JwtAuthenticationEntryPoint;
import com.platformone.booking.jwt.JwtUtils;
import com.platformone.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(SecurityConfig.class)
public class BookingControllerIntegrationTests {
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingById_Found() throws Exception {
        Booking booking = new Booking(1L, 101L, BookingStatus.CONFIRMED, 3, "PNR12345");
        Mockito.when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.scheduleId").value(101L))
                .andExpect(jsonPath("$.pnr").value("PNR12345"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingById_NotFound() throws Exception {
        Mockito.when(bookingService.getBookingById(99L)).thenReturn(null);

        mockMvc.perform(get("/booking/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateBooking_Success() throws Exception {
        BookingRequestDTO request = new BookingRequestDTO(1L, 101L, 600.0);
        BookingResponseDTO response = new BookingResponseDTO();
        response.setPnr("PNR12345");
        response.setBookingStatus(BookingStatus.CONFIRMED);

        Mockito.when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnr").value("PNR12345"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBooking_Found() throws Exception {
        Booking updatedBooking = new Booking(1L, 101L, BookingStatus.CONFIRMED, 4, "PNR12345");
        Mockito.when(bookingService.updateBooking(anyLong(), any(Booking.class))).thenReturn(Optional.of(updatedBooking));

        mockMvc.perform(put("/booking/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value(4));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBooking_NotFound() throws Exception {
        Booking updatedBooking = new Booking(1L, 101L, BookingStatus.CONFIRMED, 4, "PNR12345");
        Mockito.when(bookingService.updateBooking(anyLong(), any(Booking.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/booking/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBooking_Success() throws Exception {
        Mockito.when(bookingService.deleteBooking(1L)).thenReturn(true);

        mockMvc.perform(delete("/booking/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBooking_NotFound() throws Exception {
        Mockito.when(bookingService.deleteBooking(99L)).thenReturn(false);

        mockMvc.perform(delete("/booking/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingByPnr_Found() throws Exception {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setPnr("PNR12345");
        dto.setBookingStatus(BookingStatus.CONFIRMED);

        Mockito.when(bookingService.getBookingByPnr(anyString())).thenReturn(dto);

        mockMvc.perform(get("/booking/pnr/PNR12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("PNR12345"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingByPnr_NotFound() throws Exception {
        Mockito.when(bookingService.getBookingByPnr(anyString())).thenReturn(null);

        mockMvc.perform(get("/booking/pnr/INVALIDPNR"))
                .andExpect(status().isNotFound());
    }
}
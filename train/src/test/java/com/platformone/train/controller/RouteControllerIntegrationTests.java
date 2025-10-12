package com.platformone.train.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.train.dto.RouteCreateRequest;
import com.platformone.train.dto.RouteUpdateRequest;
import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import com.platformone.train.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
public class RouteControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Route route;
    private Train train;
    private Station station;

    @BeforeEach
    void setup() {
        train = new Train("Rajdhani Express", TrainType.EXPRESS);
        station = new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh");
        route = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        route.setTrain(train);
        route.setStation(station);
    }

    @Test
    void testGetRouteById_Success() throws Exception {
        when(routeService.getRouteById(1L)).thenReturn(route);

        mockMvc.perform(get("/route/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequenceNum").value(1))
                .andExpect(jsonPath("$.station.name").value("Kanpur Central"));
    }

    @Test
    void testGetRouteById_NotFound() throws Exception {
        when(routeService.getRouteById(999L)).thenReturn(null);

        mockMvc.perform(get("/route/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRoute() throws Exception {
        RouteCreateRequest request = new RouteCreateRequest(
                1L, 1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30)
        );

        when(routeService.createRoute(any(RouteCreateRequest.class))).thenReturn(route);

        mockMvc.perform(post("/route")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sequenceNum").value(1))
                .andExpect(jsonPath("$.station.city").value("Kanpur"));
    }

    @Test
    void testUpdateRoute_Success() throws Exception {
        RouteUpdateRequest request = new RouteUpdateRequest(
                1L, 1L, 2,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20)
        );

        when(routeService.updateRoute(eq(1L), any(RouteUpdateRequest.class)))
                .thenReturn(Optional.of(route));

        mockMvc.perform(put("/route/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station.code").value("CNB"));
    }

    @Test
    void testUpdateRoute_NotFound() throws Exception{
        RouteUpdateRequest request = new RouteUpdateRequest(
                1L, 1L, 2,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20)
        );

        when(routeService.updateRoute(eq(999L), any(RouteUpdateRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/route/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Invalid trainId or stationId"));
    }

    @Test
    void testDeleteRoute_Success() throws Exception {
        when(routeService.deleteRoute(100L)).thenReturn(true);

        mockMvc.perform(delete("/route/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Route deleted successfully"));
    }

    @Test
    void testDeleteRoute_NotFound() throws Exception {
        when(routeService.deleteRoute(999L)).thenReturn(false);

        mockMvc.perform(delete("/route/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Route not found"));
    }
}

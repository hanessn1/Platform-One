package com.platformone.train.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import com.platformone.train.jwt.CustomAccessDeniedHandler;
import com.platformone.train.jwt.JwtAuthenticationEntryPoint;
import com.platformone.train.jwt.JwtUtils;
import com.platformone.train.repository.RouteRepository;
import com.platformone.train.repository.StationRepository;
import com.platformone.train.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StationControllerIntegrationTests {
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Station station;
    private Train train;
    private Route route;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        routeRepository.deleteAll();
        stationRepository.deleteAll();
        trainRepository.deleteAll();

        station = stationRepository.save(new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh"));
        train = trainRepository.save(new Train("Rajdhani Express", TrainType.EXPRESS));
        route = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        route.setTrain(train);
        route.setStation(station);
        routeRepository.save(route);
        stationRepository.save(station);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStationById_Success() throws Exception {
        mockMvc.perform(get("/station/" + station.getStationId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kanpur Central"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStationById_NotFound() throws Exception {
        mockMvc.perform(get("/station/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateStation() throws Exception {
        Station newStation = new Station("Howrah Junction", "HWH", "Kolkata", "West Bengal");
        mockMvc.perform(post("/station")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stationId").exists())
                .andExpect(jsonPath("$.name").value("Howrah Junction"))
                .andExpect(jsonPath("$.code").value("HWH"))
                .andExpect(jsonPath("$.city").value("Kolkata"))
                .andExpect(jsonPath("$.state").value("West Bengal"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStation_Success() throws Exception {
        Station savedStation = stationRepository.save(station);
        Station updatePayload = new Station();
        updatePayload.setName("Kanpur Central");
        updatePayload.setCode("CNB");
        updatePayload.setCity("Kanpur");
        updatePayload.setState("Uttar Pradesh");

        mockMvc.perform(put("/station/" + savedStation.getStationId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kanpur Central"))
                .andExpect(jsonPath("$.code").value("CNB"))
                .andExpect(jsonPath("$.city").value("Kanpur"))
                .andExpect(jsonPath("$.state").value("Uttar Pradesh"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStation_Failure() throws Exception {
        long invalidId = 999L;
        Station updatePayload = new Station();
        updatePayload.setName("Fake Station");
        updatePayload.setCode("FAK");
        updatePayload.setCity("Nowhere");
        updatePayload.setState("NA");

        mockMvc.perform(put("/station/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStation_Success() throws Exception {
        mockMvc.perform(delete("/station/" + station.getStationId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Station deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteStation_Failure() throws Exception {
        long invalidId = 999L;
        mockMvc.perform(delete("/station/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Station not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRoutesByStationId_Success() throws Exception {
        mockMvc.perform(get("/station/" + station.getStationId() + "/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeId").value(route.getRouteId()))
                .andExpect(jsonPath("$[0].station.stationId").value(station.getStationId()))
                .andExpect(jsonPath("$[0].sequenceNum").value(route.getSequenceNum()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRoutesByStationId_NotFound() throws Exception {
        long invalidId = 999L;
        mockMvc.perform(get("/station/" + invalidId + "/route"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Station id " + invalidId + " not found"));
    }
}
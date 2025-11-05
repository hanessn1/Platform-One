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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TrainControllerIntegrationTests {
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Train train;
    private Station station;
    private Route route;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup(){
        routeRepository.deleteAll();
        stationRepository.deleteAll();
        trainRepository.deleteAll();

        train = trainRepository.save(new Train("Rajdhani Express", TrainType.EXPRESS));

        station = stationRepository.save(new Station("Kanpur Central", "CNB", "Kanpur", "Uttar Pradesh"));

        route = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        route.setTrain(train);
        route.setStation(station);
        routeRepository.save(route);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTrainById_Success() throws Exception {
        mockMvc.perform(get("/train/" + train.getTrainId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rajdhani Express"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTrainById_NotFound() throws Exception {
        mockMvc.perform(get("/train/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateTrain() throws Exception {
        Train newTrain = new Train("Duronto Express",TrainType.EXPRESS);
        mockMvc.perform(post("/train")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrain)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainId").exists())
                .andExpect(jsonPath("$.name").value("Duronto Express"))
                .andExpect(jsonPath("$.type").value(TrainType.EXPRESS.name()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTrain_Success() throws Exception {
        Train savedTrain = trainRepository.save(train);
        Train updatePayload = new Train();
        updatePayload.setName("Duronto Express");
        updatePayload.setType(TrainType.SUPERFAST);

        mockMvc.perform(put("/train/" + savedTrain.getTrainId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Duronto Express"))
                .andExpect(jsonPath("$.type").value(TrainType.SUPERFAST.name()))
                .andExpect(jsonPath("$.trainId").value(savedTrain.getTrainId()))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTrain_NotFound() throws Exception {
        long invalidId = 999L;
        Train updatePayload = new Train();
        updatePayload.setName("Duronto Express");
        updatePayload.setType(TrainType.SUPERFAST);

        mockMvc.perform(put("/train/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTrain_Success() throws Exception {
        mockMvc.perform(delete("/train/" + train.getTrainId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Train deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteTrain_NotFound() throws Exception {
        long invalidId = 999L;
        mockMvc.perform(delete("/train/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Train not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTrainBySrcDest() throws Exception {
        Station src = stationRepository.save(new Station("Howrah", "HWH", "Kolkata", "WB"));
        Station dest = stationRepository.save(new Station("Delhi", "NDLS", "Delhi", "DL"));

        Route r1 = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        r1.setStation(src);
        r1.setTrain(train);

        Route r2 = new Route(2, LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(11));
        r2.setStation(dest);
        r2.setTrain(train);

        routeRepository.saveAll(List.of(r1, r2));

        mockMvc.perform(get("/train/search")
                        .param("src", "HWH")
                        .param("dest", "NDLS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainId").exists())
                .andExpect(jsonPath("$[0].name").value("Rajdhani Express"))
                .andExpect(jsonPath("$[0].type").value("EXPRESS"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRoutesByTrainId_Success() throws Exception {
        mockMvc.perform(get("/train/" + train.getTrainId() + "/route"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeId").value(route.getRouteId()))
                .andExpect(jsonPath("$[0].station.stationId").value(station.getStationId()))
                .andExpect(jsonPath("$[0].sequenceNum").value(route.getSequenceNum()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRoutesByTrainId_NotFound() throws Exception {
        long invalidId = 999L;
        mockMvc.perform(get("/train/" + invalidId + "/route"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Train id " + invalidId + " not found"));
    }
}

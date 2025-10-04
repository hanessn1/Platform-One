package com.platformone.train.repository;

import com.platformone.train.entity.Route;
import com.platformone.train.entity.Station;
import com.platformone.train.entity.Train;
import com.platformone.train.entity.TrainType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TrainRepositoryTest {
    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private RouteRepository routeRepository;

    private Train train;

    @BeforeEach
    void setup(){
        routeRepository.deleteAll();
        trainRepository.deleteAll();
        stationRepository.deleteAll();

        train = new Train("Rajdhani Express", TrainType.EXPRESS);
        trainRepository.save(train);
    }

    @Test
    void testFindById(){
        Optional<Train> found=trainRepository.findById(train.getTrainId());
        assertTrue(found.isPresent());
        assertEquals("Rajdhani Express",found.get().getName());
    }

    @Test
    void testSaveAnotherTrain(){
        Train newTrain=new Train("Duronto Express",TrainType.EXPRESS);
        Train saved=trainRepository.save(newTrain);
        assertNotNull(saved.getTrainId());
        assertEquals("Duronto Express",saved.getName());
    }

    @Test
    void testDeleteTrain(){
        trainRepository.delete(train);
        assertFalse(trainRepository.findById(train.getTrainId()).isPresent());
    }

    @Test
    void testFindTrainsBySrcAndDest() {
        String srcCode = "HWH" + new Random().nextInt(1000);
        String destCode = "NDLS" + new Random().nextInt(1000);

        Station src = stationRepository.save(
                new Station("Howrah", srcCode, "Kolkata", "WB")
        );
        Station dest = stationRepository.save(
                new Station("Delhi", destCode, "Delhi", "DL")
        );

        Route r1 = new Route(1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        r1.setStation(src);
        r1.setTrain(train);

        Route r2 = new Route(2, LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(11));
        r2.setStation(dest);
        r2.setTrain(train);

        routeRepository.saveAll(List.of(r1, r2));

        List<Train> result = trainRepository.findTrainsBySrcAndDest(src.getCode(), dest.getCode());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Rajdhani Express");
    }
}

package com.platformone.train.repository;

import com.platformone.train.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train,Long> {

    @Query("SELECT DISTINCT t from Train t " +
            "JOIN t.routes r1 " +
            "JOIN t.routes r2 " +
            "WHERE r1.station.code = :src " +
            "AND r2.station.code = :dest " +
            "AND r1.sequenceNum < r2.sequenceNum")
    List<Train> findTrainsBySrcAndDest(
        @Param("src") String src,
        @Param("dest") String dest
    );
}

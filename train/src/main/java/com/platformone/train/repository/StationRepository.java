package com.platformone.train.repository;

import com.platformone.train.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

@Controller
public interface StationRepository extends JpaRepository<Station,Long> {
}

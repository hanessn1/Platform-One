package com.platformone.train.controller;

import com.platformone.train.entity.Station;
import com.platformone.train.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/station")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<Station> getStationById(@PathVariable long stationId){
        Station station=stationService.getStationById(stationId);
        if(station==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(station,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station newStation){
        Station savedStation=stationService.createStation(newStation);
        return new ResponseEntity<>(savedStation,HttpStatus.CREATED);
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<Station> updateStation(@PathVariable long stationId,@RequestBody Station updatedStation){
        Optional<Station> station=stationService.updateStation(stationId,updatedStation);
        if(station.isPresent())
            return new ResponseEntity<>(station.get(),HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<String> deleteStation(@PathVariable long stationId){
        boolean deleted=stationService.deleteStation(stationId);
        if(deleted)
            return new ResponseEntity<>("Station deleted successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Station not found",HttpStatus.NOT_FOUND);
    }
}

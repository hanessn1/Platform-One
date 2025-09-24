package com.platformone.train.controller;

import com.platformone.train.entity.Route;
import com.platformone.train.entity.Train;
import com.platformone.train.service.RouteService;
import com.platformone.train.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/train")
public class TrainController {
    private final TrainService trainService;
    private final RouteService routeService;

    public TrainController(TrainService trainService, RouteService routeService) {
        this.trainService = trainService;
        this.routeService = routeService;
    }

    @GetMapping("/{trainId}")
    public ResponseEntity<Train> getTrainById(@PathVariable long trainId) {
        Train train = trainService.getTrainById(trainId);
        if (train == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(train, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody Train newTrain) {
        Train savedTrain = trainService.createTrain(newTrain);
        return new ResponseEntity<>(savedTrain, HttpStatus.CREATED);
    }

    @PutMapping("/{trainId}")
    public ResponseEntity<Train> updateTrain(@PathVariable long trainId, @RequestBody Train updatedTrain) {
        Optional<Train> train = trainService.updateTrain(trainId, updatedTrain);
        if (train.isPresent())
            return new ResponseEntity<>(train.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{trainId}")
    public ResponseEntity<String> deleteTrain(@PathVariable long trainId) {
        boolean deleted = trainService.deleteTrain(trainId);
        if (deleted)
            return new ResponseEntity<>("Train deleted successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Train not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{trainId}/route")
    public ResponseEntity<?> getRoutesByTrainId(@PathVariable long trainId) {
        Train train = trainService.getTrainById(trainId);
        if (train == null)
            return new ResponseEntity<>("Train id " + trainId + " not found", HttpStatus.NOT_FOUND);
        List<Route> routes = routeService.getRoutesByTrainId(trainId);
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Train>> getTrainBySrcDest(@RequestParam String src, @RequestParam String dest) {
        return new ResponseEntity<>(trainService.getTrainBySrcDest(src,dest),HttpStatus.OK);
    }
}

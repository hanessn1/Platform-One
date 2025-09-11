package com.platformone.train.controller;

import com.platformone.train.dto.RouteCreateRequest;
import com.platformone.train.dto.RouteUpdateRequest;
import com.platformone.train.entity.Route;
import com.platformone.train.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/route")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<Route> getRouteById(@PathVariable long routeId){
        Route route=routeService.getRouteById(routeId);
        if(route==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(route,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createRoute(@RequestBody RouteCreateRequest routeRequest){
        Route savedRoute=routeService.createRoute(routeRequest);
        if(savedRoute==null)
            return new ResponseEntity<>("Invalid trainId or stationId",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(savedRoute,HttpStatus.CREATED);
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<Object> updateRoute(@PathVariable long routeId,@RequestBody RouteUpdateRequest routeRequest){
        Optional<Route> route=routeService.updateRoute(routeId,routeRequest);
        if(route.isPresent())
            return new ResponseEntity<>(route.get(),HttpStatus.OK);
        return new ResponseEntity<>("Invalid trainId or stationId",HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<String> deleteRoute(@PathVariable long routeId){
        boolean deleted=routeService.deleteRoute(routeId);
        if(deleted)
            return new ResponseEntity<>("Route deleted successfully",HttpStatus.OK);
        return new ResponseEntity<>("Route not found",HttpStatus.NOT_FOUND);
    }
}

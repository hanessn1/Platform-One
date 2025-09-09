package com.platformone.train.service;

import com.platformone.train.dto.RouteCreateRequest;
import com.platformone.train.dto.RouteUpdateRequest;
import com.platformone.train.entity.Route;

import java.util.Optional;

public interface RouteService {
    Route getRouteById(long routeId);

    Route createRoute(RouteCreateRequest routeRequest);

    Optional<Route> updateRoute(long routeId, RouteUpdateRequest routeRequest);

    boolean deleteRoute(long routeId);
}

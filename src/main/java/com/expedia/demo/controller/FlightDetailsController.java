package com.expedia.demo.controller;

import com.amadeus.exceptions.ResponseException;
import com.expedia.demo.service.FlightDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FlightDetailsController {
    private final FlightDetailsService flightDetailsService;

    @Autowired
    public FlightDetailsController(FlightDetailsService flightDetailsService) {
        this.flightDetailsService = flightDetailsService;
    }

    @GetMapping("/flight-details/{flightId}")
    public ResponseEntity<Map<String, Object>> getFlightDetails(@PathVariable String flightId) {
        try {
            Map<String, Object> details = flightDetailsService.getFlightDetails(flightId);
            if (details == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(details);
        } catch (ResponseException e) {
            return ResponseEntity.status(500).build();
        }
    }
}


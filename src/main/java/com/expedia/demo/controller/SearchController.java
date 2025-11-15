package com.expedia.demo.controller;

import com.expedia.demo.model.Flight;
import com.expedia.demo.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date) {
        logger.info("Received flight search request - origin: {}, destination: {}, date: {}", origin, destination, date);
        List<Flight> flights = searchService.searchFlights(origin, destination, date);
        return ResponseEntity.ok(flights);
    }
}


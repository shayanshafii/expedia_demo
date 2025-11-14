package com.expedia.demo.controller;

import com.expedia.demo.model.Flight;
import com.expedia.demo.service.SearchService;
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
        List<Flight> flights = searchService.searchFlights(origin, destination, date);
        return ResponseEntity.ok(flights);
    }
}


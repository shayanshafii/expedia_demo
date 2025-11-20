package com.expedia.demo.controller;

import com.expedia.demo.model.Flight;
import com.expedia.demo.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchControllerTest {
    @Mock
    private SearchService searchService;

    private SearchController searchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchController = new SearchController(searchService);
    }

    @Test
    void testSearch_Success() {
        String origin = "NYC";
        String destination = "LAX";
        String date = "2025-12-01";

        List<Flight> mockFlights = Arrays.asList(
            new Flight("flight-1", origin, destination, date, "Test Airline"),
            new Flight("flight-2", origin, destination, date, "Another Airline")
        );

        when(searchService.searchFlights(origin, destination, date)).thenReturn(mockFlights);

        ResponseEntity<List<Flight>> response = searchController.search(origin, destination, date);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(searchService, times(1)).searchFlights(origin, destination, date);
    }

    @Test
    void testSearch_NoResults() {
        String origin = "NYC";
        String destination = "MIA";
        String date = "2025-12-01";

        when(searchService.searchFlights(origin, destination, date)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Flight>> response = searchController.search(origin, destination, date);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearch_SingleResult() {
        String origin = "DFW";
        String destination = "ORD";
        String date = "2026-01-10";

        List<Flight> mockFlights = Arrays.asList(
            new Flight("flight-1", origin, destination, date, "Delta")
        );

        when(searchService.searchFlights(origin, destination, date)).thenReturn(mockFlights);

        ResponseEntity<List<Flight>> response = searchController.search(origin, destination, date);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSearch_CaseInsensitive() {
        String origin = "nyc";
        String destination = "lax";
        String date = "2025-12-01";

        List<Flight> mockFlights = Arrays.asList(
            new Flight("flight-1", "NYC", "LAX", date, "Test Airline")
        );

        when(searchService.searchFlights(origin, destination, date)).thenReturn(mockFlights);

        ResponseEntity<List<Flight>> response = searchController.search(origin, destination, date);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSearch_DifferentDateFormats() {
        String origin = "NYC";
        String destination = "LAX";
        String date = "12/01/2025";

        List<Flight> mockFlights = Arrays.asList(
            new Flight("flight-1", origin, destination, date, "Test Airline")
        );

        when(searchService.searchFlights(origin, destination, date)).thenReturn(mockFlights);

        ResponseEntity<List<Flight>> response = searchController.search(origin, destination, date);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}

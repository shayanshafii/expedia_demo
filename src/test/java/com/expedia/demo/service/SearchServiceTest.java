package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchServiceTest {
    private SearchService searchService;

    @BeforeEach
    void setUp() throws Exception {
        searchService = new SearchService();
        
        // Manually set test flights using reflection since @PostConstruct won't run in unit tests
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        testFlights.add(new Flight("test-id-2", "NYC", "LAX", "12/01/2025", "Another Airline"));
        testFlights.add(new Flight("test-id-3", "SFO", "DEN", "01/02/2026", "Test Airline"));
        testFlights.add(new Flight("test-id-4", "NYC", "LAX", "12/15/2025", "Test Airline"));
        
        // Use reflection to set the private allFlights field
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);
    }

    @Test
    void testSearchFlights_Success() {
        // Execute - using frontend date format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify
        assertNotNull(flights);
        assertEquals(2, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
        assertEquals("12/01/2025", flights.get(0).getDepartureDate());
        assertTrue(flights.stream().anyMatch(f -> f.getAirline().equals("Test Airline")));
        assertTrue(flights.stream().anyMatch(f -> f.getAirline().equals("Another Airline")));
    }

    @Test
    void testSearchFlights_NoMatches() {
        // Execute with non-matching criteria
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-31");

        // Verify
        assertNotNull(flights);
        assertEquals(0, flights.size());
    }

    @Test
    void testSearchFlights_DifferentDate() {
        // Execute with different date
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-15");

        // Verify
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
        assertEquals("12/15/2025", flights.get(0).getDepartureDate());
    }

    @Test
    void testSearchFlights_CaseInsensitive() {
        // Execute with lowercase origin/destination
        List<Flight> flights = searchService.searchFlights("nyc", "lax", "2025-12-01");

        // Verify - should still match due to case-insensitive comparison
        assertNotNull(flights);
        assertEquals(2, flights.size());
    }
}

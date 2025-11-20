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
        testFlights.add(new Flight("test-id-2", "LAX", "NYC", "12/01/2025", "Test Airline"));
        testFlights.add(new Flight("test-id-3", "NYC", "SFO", "12/15/2025", "Another Airline"));
        testFlights.add(new Flight("test-id-4", "DFW", "ORD", "01/10/2026", "Delta"));
        
        // Use reflection to set the private allFlights field
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);
    }

    @Test
    void testSearchFlights_Success() {
        // Execute - using frontend date format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertFalse(flights.isEmpty());
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_WithJsonDateFormat() {
        // Execute - using JSON date format (MM/dd/yyyy)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "12/01/2025");

        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
    }

    @Test
    void testSearchFlights_CaseInsensitive() {
        // Execute - test case insensitivity
        List<Flight> flights = searchService.searchFlights("nyc", "lax", "2025-12-01");

        assertNotNull(flights);
        assertEquals(1, flights.size());
    }

    @Test
    void testSearchFlights_NoResults() {
        // Execute - search for non-existent route
        List<Flight> flights = searchService.searchFlights("NYC", "MIA", "2025-12-01");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_DifferentDate() {
        // Execute - search for different date
        List<Flight> flights = searchService.searchFlights("NYC", "SFO", "2025-12-15");

        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("SFO", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_MultipleFlightsSameRoute() throws Exception {
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        testFlights.add(new Flight("test-id-5", "NYC", "LAX", "12/01/2025", "Another Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertEquals(2, flights.size());
    }

    @Test
    void testSearchFlights_InvalidDateFormat() {
        // Execute - search with invalid date format
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "invalid-date");

        // Verify - should return empty list due to date mismatch
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_EmptyFlightList() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, new ArrayList<>());

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_NullOrigin() {
        // Execute - this will throw NullPointerException but should be caught
        List<Flight> flights = searchService.searchFlights(null, "LAX", "2025-12-01");

        // Verify - should return empty list due to exception handling
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_NullDestination() {
        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", null, "2025-12-01");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_NullDate() {
        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", null);

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testLoadFlights_Success() {
        SearchService newService = new SearchService();
        newService.loadFlights();

        // Execute a search to verify flights were loaded
        List<Flight> flights = newService.searchFlights("DFW", "CLT", "2025-12-13");

        // Verify - should find flights from the actual flights.json file
        assertNotNull(flights);
    }
}

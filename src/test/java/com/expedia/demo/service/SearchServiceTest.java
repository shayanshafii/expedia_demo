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
        testFlights.add(new Flight("test-id-2", "SFO", "ORD", "12/15/2025", "Delta"));
        testFlights.add(new Flight("test-id-3", "nyc", "lax", "12/01/2025", "United"));
        
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
        assertEquals(2, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_NoMatch_ReturnsEmptyList() {
        // Execute - search for non-existent route
        List<Flight> flights = searchService.searchFlights("ATL", "MIA", "2025-12-01");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_CaseInsensitive() {
        // Execute - search with lowercase
        List<Flight> flights = searchService.searchFlights("nyc", "lax", "2025-12-01");

        // Verify - should match both NYC and nyc origins
        assertNotNull(flights);
        assertEquals(2, flights.size());
    }

    @Test
    void testSearchFlights_WithJsonDateFormat() {
        // Execute - using JSON date format (MM/dd/yyyy)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "12/01/2025");

        assertNotNull(flights);
        assertEquals(2, flights.size());
    }

    @Test
    void testSearchFlights_InvalidDateFormat() {
        // Execute - using invalid date format
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025/12/01");

        // Verify - should still return results or empty list without crashing
        assertNotNull(flights);
    }

    @Test
    void testSearchFlights_ExceptionHandling() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, null);

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should return empty list on error
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testLoadFlights_LoadsFromResource() {
        SearchService newService = new SearchService();
        newService.loadFlights();

        // Execute search to verify flights were loaded
        List<Flight> flights = newService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should find flights from test resources
        assertNotNull(flights);
        assertFalse(flights.isEmpty());
    }

    @Test
    void testGenerateFlightId_Deterministic() throws Exception {
        SearchService service1 = new SearchService();
        service1.loadFlights();
        
        SearchService service2 = new SearchService();
        service2.loadFlights();

        List<Flight> flights1 = service1.searchFlights("NYC", "LAX", "2025-12-01");
        List<Flight> flights2 = service2.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - same origin/destination/airline should produce same flight ID
        assertFalse(flights1.isEmpty());
        assertFalse(flights2.isEmpty());
        assertEquals(flights1.get(0).getFlightId(), flights2.get(0).getFlightId());
    }

    @Test
    void testGenerateFlightId_Different() {
        // Execute - search for different flights
        List<Flight> nycFlights = searchService.searchFlights("NYC", "LAX", "2025-12-01");
        List<Flight> sfoFlights = searchService.searchFlights("SFO", "ORD", "2025-12-15");

        // Verify - different routes should have different flight IDs
        assertFalse(nycFlights.isEmpty());
        assertFalse(sfoFlights.isEmpty());
        assertNotEquals(nycFlights.get(0).getFlightId(), sfoFlights.get(0).getFlightId());
    }
}

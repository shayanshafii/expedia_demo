package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        testFlights.add(new Flight("test-id-2", "SFO", "ORD", "12/15/2025", "Another Airline"));
        
        // Use reflection to set the private allFlights field
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);
    }

    @Test
    void testSearchFlights_Success() {
        // Execute - using frontend date format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - enhanced assertions
        assertNotNull(flights);
        assertFalse(flights.isEmpty());
        assertEquals(1, flights.size());
        
        Flight flight = flights.get(0);
        assertEquals("NYC", flight.getOrigin());
        assertEquals("LAX", flight.getDestination());
        assertEquals("12/01/2025", flight.getDepartureDate());
        assertEquals("Test Airline", flight.getAirline());
    }

    @Test
    void testSearchFlights_NoMatches() {
        // Execute - search for non-existent route
        List<Flight> flights = searchService.searchFlights("ATL", "MIA", "2025-12-01");

        // Verify - should return empty list
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
        "2025-12-01, NYC, LAX",  // Frontend format (yyyy-MM-dd)
        "12/01/2025, NYC, LAX",  // JSON format (MM/dd/yyyy)
        "2025-12-15, SFO, ORD",  // Frontend format for second flight
        "12/15/2025, SFO, ORD"   // JSON format for second flight
    })
    void testSearchFlights_DateNormalization(String dateInput, String origin, String destination) {
        // Execute - test with different date formats
        List<Flight> flights = searchService.searchFlights(origin, destination, dateInput);

        // Verify - should find the flight regardless of date format
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals(origin, flights.get(0).getOrigin());
        assertEquals(destination, flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_EmptyFlightList() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, new ArrayList<>());

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should return empty list
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_CaseInsensitiveSearch() {
        // Execute - test with different cases
        List<Flight> flights1 = searchService.searchFlights("nyc", "lax", "2025-12-01");
        List<Flight> flights2 = searchService.searchFlights("NYC", "LAX", "2025-12-01");
        List<Flight> flights3 = searchService.searchFlights("Nyc", "Lax", "2025-12-01");

        // Verify - all should return the same result
        assertNotNull(flights1);
        assertNotNull(flights2);
        assertNotNull(flights3);
        assertEquals(1, flights1.size());
        assertEquals(1, flights2.size());
        assertEquals(1, flights3.size());
    }
}

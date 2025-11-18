package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        
        // Use reflection to set the private allFlights field
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);
    }

    @Test
    void testSearchFlights_Success() {
        // Execute - using frontend date format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - minimal assertions
        assertNotNull(flights);
        assertFalse(flights.isEmpty());
    }

    @Test
    void testSearchFlights_NoMatches() throws Exception {
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        testFlights.add(new Flight("test-id-2", "SEA", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search for route that doesn't exist
        List<Flight> flights = searchService.searchFlights("NYC", "SFO", "2025-12-01");

        // Verify - should return empty list
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_CaseInsensitive() throws Exception {
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NyC", "lAx", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with lowercase
        List<Flight> flights = searchService.searchFlights("nyc", "lax", "2025-12-01");

        // Verify - should find the flight despite case differences
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NyC", flights.get(0).getOrigin());
        assertEquals("lAx", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_FrontendDateFormat() throws Exception {
        // Setup
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with frontend format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should match flight with JSON format date
        assertNotNull(flights);
        assertEquals(1, flights.size());
    }

    @Test
    void testSearchFlights_JsonDateFormat() throws Exception {
        // Setup
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with JSON format (MM/dd/yyyy)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "12/01/2025");

        // Verify - should match flight
        assertNotNull(flights);
        assertEquals(1, flights.size());
    }

    @Test
    void testSearchFlights_InvalidDateFormat() throws Exception {
        // Setup
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with invalid date format
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025/12/01");

        // Verify - should return empty (date format doesn't match)
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_MultipleMatches() throws Exception {
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Delta"));
        testFlights.add(new Flight("test-id-2", "NYC", "LAX", "12/01/2025", "United"));
        testFlights.add(new Flight("test-id-3", "NYC", "SFO", "12/01/2025", "Delta"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should return both matching flights
        assertNotNull(flights);
        assertEquals(2, flights.size());
    }

    @Test
    void testSearchFlights_ErrorHandling() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, null);

        // Execute - should handle error gracefully
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should return empty list instead of throwing exception
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_NullDate() throws Exception {
        // Setup
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with null date
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", null);

        // Verify - should handle gracefully and return empty
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_EmptyDate() throws Exception {
        // Setup
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("test-id-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        // Execute - search with empty date
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "");

        // Verify - should return empty (invalid date)
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testNormalizeDate_FrontendFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - frontend format should be converted to JSON format
        String result = (String) normalizeDateMethod.invoke(searchService, "2025-12-01");

        assertEquals("12/01/2025", result);
    }

    @Test
    void testNormalizeDate_JsonFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - JSON format should be returned as-is
        String result = (String) normalizeDateMethod.invoke(searchService, "12/31/2025");

        assertEquals("12/31/2025", result);
    }

    @Test
    void testNormalizeDate_InvalidFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - invalid format should be returned as-is
        String result = (String) normalizeDateMethod.invoke(searchService, "2025/12/01");

        // Verify - returns original string when format doesn't match
        assertEquals("2025/12/01", result);
    }

    @Test
    void testNormalizeDate_NullDate() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - null should be handled gracefully
        String result = (String) normalizeDateMethod.invoke(searchService, (String) null);

        // Verify - returns null when input is null
        assertNull(result);
    }
}

package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import com.expedia.demo.model.FlightDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        testFlights.add(new Flight("test-id-2", "SFO", "BOS", "12/15/2025", "Delta"));
        testFlights.add(new Flight("test-id-3", "NYC", "LAX", "12/20/2025", "United"));
        
        // Use reflection to set the private allFlights field
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);
    }

    @Test
    void testSearchFlights_Success_FrontendDateFormat() {
        // Execute - using frontend date format (YYYY-MM-DD)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
        assertEquals("12/01/2025", flights.get(0).getDepartureDate());
    }

    @Test
    void testSearchFlights_Success_JsonDateFormat() {
        // Execute - using JSON date format (MM/dd/yyyy)
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "12/01/2025");

        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_CaseInsensitive() {
        // Execute - using lowercase origin and mixed case destination
        List<Flight> flights = searchService.searchFlights("nyc", "lAx", "2025-12-01");

        // Verify - should still find the flight
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_NoMatches() {
        // Execute - search for non-existent route
        List<Flight> flights = searchService.searchFlights("NYC", "MIA", "2025-12-01");

        // Verify - should return empty list
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_ExceptionHandling() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, null);

        // Execute - should catch exception and return empty list
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify - should return empty list instead of throwing exception
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_UnknownDateFormat() {
        // Execute - using invalid date format
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "bogus-date");

        // Verify - should return empty list (no matches due to date mismatch)
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
        "2025-13-01",  // Invalid month
        "2025-12-32",  // Invalid day
        "2025-00-01",  // Invalid month (zero)
        "2025-12-00"   // Invalid day (zero)
    })
    void testSearchFlights_MalformedDateFormat(String malformedDate) {
        // Execute - using malformed but pattern-matching date
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", malformedDate);

        // Verify - should return empty list (normalizeDate catches exception)
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testLoadFlights_PopulatesFlightsList() throws Exception {
        SearchService newService = new SearchService();

        // Execute - call loadFlights
        newService.loadFlights();

        // Verify - allFlights should be populated from flights.json
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Flight> loadedFlights = (List<Flight>) allFlightsField.get(newService);

        assertNotNull(loadedFlights);
        assertTrue(loadedFlights.size() > 0, "Should load flights from flights.json");
        
        // Verify that flights have proper IDs (16 character hex strings)
        Flight firstFlight = loadedFlights.get(0);
        assertNotNull(firstFlight.getFlightId());
        assertEquals(16, firstFlight.getFlightId().length());
        assertTrue(firstFlight.getFlightId().matches("[0-9a-f]+"), 
            "Flight ID should be hexadecimal");
    }

    @Test
    void testGenerateFlightId_Deterministic() throws Exception {
        Method generateFlightIdMethod = SearchService.class.getDeclaredMethod(
            "generateFlightId", String.class, String.class, String.class);
        generateFlightIdMethod.setAccessible(true);

        // Execute - generate ID twice with same inputs
        String id1 = (String) generateFlightIdMethod.invoke(searchService, "NYC", "LAX", "Delta");
        String id2 = (String) generateFlightIdMethod.invoke(searchService, "NYC", "LAX", "Delta");

        // Verify - IDs should be identical and properly formatted
        assertNotNull(id1);
        assertNotNull(id2);
        assertEquals(id1, id2, "Flight IDs should be deterministic");
        assertEquals(16, id1.length(), "Flight ID should be 16 characters");
        assertTrue(id1.matches("[0-9a-f]+"), "Flight ID should be hexadecimal");
    }

    @Test
    void testGenerateFlightId_DifferentInputs() throws Exception {
        Method generateFlightIdMethod = SearchService.class.getDeclaredMethod(
            "generateFlightId", String.class, String.class, String.class);
        generateFlightIdMethod.setAccessible(true);

        // Execute - generate IDs with different inputs
        String id1 = (String) generateFlightIdMethod.invoke(searchService, "NYC", "LAX", "Delta");
        String id2 = (String) generateFlightIdMethod.invoke(searchService, "SFO", "BOS", "United");

        // Verify - IDs should be different
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2, "Different inputs should produce different IDs");
    }

    @Test
    void testConvertToFlight_SetsFieldsCorrectly() throws Exception {
        FlightDTO dto = new FlightDTO();
        dto.setOrigin("NYC");
        dto.setDestination("LAX");
        dto.setDate("12/01/2025");
        dto.setAirline("Delta");

        Method convertToFlightMethod = SearchService.class.getDeclaredMethod(
            "convertToFlight", FlightDTO.class);
        convertToFlightMethod.setAccessible(true);

        // Execute
        Flight flight = (Flight) convertToFlightMethod.invoke(searchService, dto);

        // Verify - all fields should be set correctly
        assertNotNull(flight);
        assertEquals("NYC", flight.getOrigin());
        assertEquals("LAX", flight.getDestination());
        assertEquals("12/01/2025", flight.getDepartureDate());
        assertEquals("Delta", flight.getAirline());
        assertNotNull(flight.getFlightId());
        assertEquals(16, flight.getFlightId().length());
    }

    @Test
    void testNormalizeDate_FrontendFormat() throws Exception {
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod(
            "normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - normalize frontend format
        String normalized = (String) normalizeDateMethod.invoke(searchService, "2025-12-01");

        // Verify - should convert to JSON format
        assertEquals("12/01/2025", normalized);
    }

    @Test
    void testNormalizeDate_JsonFormat() throws Exception {
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod(
            "normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - normalize JSON format (should return as-is)
        String normalized = (String) normalizeDateMethod.invoke(searchService, "12/01/2025");

        // Verify - should remain unchanged
        assertEquals("12/01/2025", normalized);
    }

    @Test
    void testNormalizeDate_UnknownFormat() throws Exception {
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod(
            "normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - normalize unknown format
        String normalized = (String) normalizeDateMethod.invoke(searchService, "unknown-format");

        // Verify - should return original string
        assertEquals("unknown-format", normalized);
    }

    @Test
    void testNormalizeDate_MalformedFrontendFormat() throws Exception {
        Method normalizeDateMethod = SearchService.class.getDeclaredMethod(
            "normalizeDate", String.class);
        normalizeDateMethod.setAccessible(true);

        // Execute - normalize malformed date that matches pattern but is invalid
        String normalized = (String) normalizeDateMethod.invoke(searchService, "2025-13-01");

        // Verify - should return original string (exception caught)
        assertEquals("2025-13-01", normalized);
    }
}

package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import com.expedia.demo.model.FlightDTO;
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
        testFlights.add(new Flight("test-id-2", "LAX", "NYC", "12/15/2025", "Another Airline"));
        testFlights.add(new Flight("test-id-3", "SFO", "ORD", "01/10/2026", "Delta"));
        
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
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_NoResults() {
        List<Flight> flights = searchService.searchFlights("NYC", "SFO", "2025-12-01");

        // Verify empty results
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testSearchFlights_CaseInsensitive() {
        List<Flight> flights = searchService.searchFlights("nyc", "lax", "2025-12-01");

        // Verify case insensitivity works
        assertNotNull(flights);
        assertEquals(1, flights.size());
    }

    @Test
    void testSearchFlights_JsonDateFormat() {
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "12/01/2025");

        // Verify it works with JSON format
        assertNotNull(flights);
        assertEquals(1, flights.size());
    }

    @Test
    void testSearchFlights_DifferentRoute() {
        List<Flight> flights = searchService.searchFlights("LAX", "NYC", "2025-12-15");

        // Verify correct flight is returned
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("LAX", flights.get(0).getOrigin());
        assertEquals("NYC", flights.get(0).getDestination());
    }

    @Test
    void testSearchFlights_WrongDate() {
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-02");

        // Verify no results
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }

    @Test
    void testLoadFlights() {
        SearchService newService = new SearchService();
        newService.loadFlights();

        // Verify flights were loaded (should load from flights.json)
        List<Flight> flights = newService.searchFlights("DFW", "CLT", "2025-12-13");
        assertNotNull(flights);
    }

    @Test
    void testConvertToFlight() throws Exception {
        FlightDTO dto = new FlightDTO();
        dto.setOrigin("JFK");
        dto.setDestination("LAX");
        dto.setDate("12/25/2025");
        dto.setAirline("United");

        // Use reflection to call private convertToFlight method
        Method convertMethod = SearchService.class.getDeclaredMethod("convertToFlight", FlightDTO.class);
        convertMethod.setAccessible(true);
        Flight flight = (Flight) convertMethod.invoke(searchService, dto);

        // Verify conversion
        assertNotNull(flight);
        assertEquals("JFK", flight.getOrigin());
        assertEquals("LAX", flight.getDestination());
        assertEquals("12/25/2025", flight.getDepartureDate());
        assertEquals("United", flight.getAirline());
        assertNotNull(flight.getFlightId());
    }

    @Test
    void testGenerateFlightId() throws Exception {
        // Use reflection to call private generateFlightId method
        Method generateMethod = SearchService.class.getDeclaredMethod("generateFlightId", String.class, String.class, String.class);
        generateMethod.setAccessible(true);
        
        String flightId1 = (String) generateMethod.invoke(searchService, "NYC", "LAX", "Delta");
        assertNotNull(flightId1);
        assertEquals(16, flightId1.length());

        String flightId2 = (String) generateMethod.invoke(searchService, "NYC", "LAX", "Delta");
        assertEquals(flightId1, flightId2);

        String flightId3 = (String) generateMethod.invoke(searchService, "LAX", "NYC", "Delta");
        assertNotEquals(flightId1, flightId3);
    }

    @Test
    void testNormalizeDate_FrontendFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeMethod.setAccessible(true);

        String normalized = (String) normalizeMethod.invoke(searchService, "2025-12-01");
        assertEquals("12/01/2025", normalized);
    }

    @Test
    void testNormalizeDate_JsonFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeMethod.setAccessible(true);

        String normalized = (String) normalizeMethod.invoke(searchService, "12/01/2025");
        assertEquals("12/01/2025", normalized);
    }

    @Test
    void testNormalizeDate_InvalidFormat() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeMethod.setAccessible(true);

        String invalidDate = "invalid-date";
        String normalized = (String) normalizeMethod.invoke(searchService, invalidDate);
        assertEquals(invalidDate, normalized);
    }

    @Test
    void testNormalizeDate_EdgeCase() throws Exception {
        // Use reflection to call private normalizeDate method
        Method normalizeMethod = SearchService.class.getDeclaredMethod("normalizeDate", String.class);
        normalizeMethod.setAccessible(true);

        String weirdDate = "2025/12/01";
        String normalized = (String) normalizeMethod.invoke(searchService, weirdDate);
        assertEquals(weirdDate, normalized);
    }

    @Test
    void testSearchFlights_MultipleMatches() throws Exception {
        List<Flight> testFlights = new ArrayList<>();
        testFlights.add(new Flight("id-1", "NYC", "LAX", "12/01/2025", "Delta"));
        testFlights.add(new Flight("id-2", "NYC", "LAX", "12/01/2025", "United"));
        testFlights.add(new Flight("id-3", "NYC", "LAX", "12/01/2025", "American"));
        
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, testFlights);

        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertEquals(3, flights.size());
    }

    @Test
    void testSearchFlights_EmptyFlightsList() throws Exception {
        Field allFlightsField = SearchService.class.getDeclaredField("allFlights");
        allFlightsField.setAccessible(true);
        allFlightsField.set(searchService, new ArrayList<Flight>());

        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }
}

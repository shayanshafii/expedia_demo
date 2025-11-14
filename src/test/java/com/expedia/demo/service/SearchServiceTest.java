package com.expedia.demo.service;

import com.expedia.demo.model.AviationstackRouteResponse;
import com.expedia.demo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SearchServiceTest {
    @Mock
    private RestTemplate restTemplate;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchService = new SearchService(restTemplate, "test-key", "https://api.aviationstack.com/v1");
    }

    @Test
    void testSearchFlights_Success() {
        // Create mock response
        AviationstackRouteResponse response = new AviationstackRouteResponse();
        AviationstackRouteResponse.Route route = new AviationstackRouteResponse.Route();
        AviationstackRouteResponse.Departure departure = new AviationstackRouteResponse.Departure();
        departure.setIata("NYC");
        AviationstackRouteResponse.Arrival arrival = new AviationstackRouteResponse.Arrival();
        arrival.setIata("LAX");
        AviationstackRouteResponse.Airline airline = new AviationstackRouteResponse.Airline();
        airline.setName("Test Airline");
        airline.setIata("TA");
        
        route.setDeparture(departure);
        route.setArrival(arrival);
        route.setAirline(airline);
        response.setData(List.of(route));

        // Setup mocks
        when(restTemplate.getForObject(anyString(), eq(AviationstackRouteResponse.class))).thenReturn(response);

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify
        assertNotNull(flights);
        assertEquals(1, flights.size());
        assertEquals("NYC", flights.get(0).getOrigin());
        assertEquals("LAX", flights.get(0).getDestination());
        assertEquals("Test Airline", flights.get(0).getAirline());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(AviationstackRouteResponse.class));
    }
}

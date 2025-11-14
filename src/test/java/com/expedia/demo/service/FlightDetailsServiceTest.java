package com.expedia.demo.service;

import com.amadeus.Amadeus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class FlightDetailsServiceTest {
    @Mock
    private Amadeus amadeus;

    private FlightDetailsService flightDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightDetailsService = new FlightDetailsService(amadeus);
    }

    @Test
    void testFlightDetailsServiceExists() {
        assertNotNull(flightDetailsService);
    }
}

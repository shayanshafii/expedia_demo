package com.expedia.demo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import com.expedia.demo.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SearchServiceTest {
    @Mock
    private Amadeus amadeus;

    @Mock
    private Amadeus.Shopping shopping;

    @Mock
    private Amadeus.Shopping.FlightOffersSearch flightOffersSearch;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchService = new SearchService(amadeus);
    }

    @Test
    void testSearchFlights_Success() throws Exception {
        // Create mock flight offer
        FlightOfferSearch mockOffer = createMockFlightOffer();
        FlightOfferSearch[] offers = {mockOffer};

        // Setup mocks
        when(amadeus.shopping).thenReturn(shopping);
        when(shopping.flightOffersSearch).thenReturn(flightOffersSearch);
        when(flightOffersSearch.get(any(Params.class))).thenReturn(offers);

        // Execute
        List<Flight> flights = searchService.searchFlights("NYC", "LAX", "2025-12-01");

        // Verify
        assertNotNull(flights);
        assertEquals(1, flights.size());
        verify(flightOffersSearch, times(1)).get(any(Params.class));
    }

    private FlightOfferSearch createMockFlightOffer() throws Exception {
        FlightOfferSearch offer = mock(FlightOfferSearch.class);
        when(offer.getId()).thenReturn("test-flight-id");

        // Mock price
        FlightOfferSearch.Price price = mock(FlightOfferSearch.Price.class);
        when(price.getTotal()).thenReturn("500.00");
        when(offer.getPrice()).thenReturn(price);

        // Mock validating airline codes
        when(offer.getValidatingAirlineCodes()).thenReturn(new String[]{"AA"});

        // Mock itinerary and segments
        FlightOfferSearch.Itinerary itinerary = mock(FlightOfferSearch.Itinerary.class);
        FlightOfferSearch.Segment segment = mock(FlightOfferSearch.Segment.class);
        FlightOfferSearch.FlightEndpoint departure = mock(FlightOfferSearch.FlightEndpoint.class);
        FlightOfferSearch.FlightEndpoint arrival = mock(FlightOfferSearch.FlightEndpoint.class);

        when(departure.getIataCode()).thenReturn("NYC");
        when(departure.getAt()).thenReturn("2025-12-01T10:00:00");
        when(arrival.getIataCode()).thenReturn("LAX");
        when(segment.getDeparture()).thenReturn(departure);
        when(segment.getArrival()).thenReturn(arrival);
        when(itinerary.getSegments()).thenReturn(new FlightOfferSearch.Segment[]{segment});
        when(offer.getItineraries()).thenReturn(new FlightOfferSearch.Itinerary[]{itinerary});

        return offer;
    }
}


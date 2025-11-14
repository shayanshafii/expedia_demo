package com.expedia.demo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.FlightPrice;
import com.expedia.demo.service.FlightDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FlightDetailsServiceTest {
    @Mock
    private Amadeus amadeus;

    @Mock
    private Amadeus.Shopping shopping;

    @Mock
    private Amadeus.Shopping.FlightOffersSearch flightOffersSearch;

    @Mock
    private Amadeus.Shopping.FlightOffersSearch.Pricing pricing;

    private FlightDetailsService flightDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightDetailsService = new FlightDetailsService(amadeus);
    }

    @Test
    void testGetFlightDetails_Success() throws Exception {
        String flightId = "test-flight-id";

        // Mock flight offer
        FlightOfferSearch mockOffer = mock(FlightOfferSearch.class);
        FlightOfferSearch[] offers = {mockOffer};

        // Mock flight price
        FlightPrice mockPrice = createMockFlightPrice();

        // Setup mocks
        when(amadeus.shopping).thenReturn(shopping);
        when(shopping.flightOffersSearch).thenReturn(flightOffersSearch);
        when(flightOffersSearch.get(any(Params.class))).thenReturn(offers);
        when(flightOffersSearch.pricing).thenReturn(pricing);
        when(pricing.post(any(FlightOfferSearch.class))).thenReturn(mockPrice);

        // Execute
        Map<String, Object> details = flightDetailsService.getFlightDetails(flightId);

        // Verify
        assertNotNull(details);
        assertEquals(flightId, details.get("flight_id"));
        verify(flightOffersSearch, times(1)).get(any(Params.class));
        verify(pricing, times(1)).post(any(FlightOfferSearch.class));
    }

    private FlightPrice createMockFlightPrice() {
        FlightPrice price = mock(FlightPrice.class);
        FlightPrice.FlightOffer[] flightOffers = new FlightPrice.FlightOffer[1];
        FlightPrice.FlightOffer offer = mock(FlightPrice.FlightOffer.class);

        FlightPrice.Price priceObj = mock(FlightPrice.Price.class);
        when(priceObj.getTotal()).thenReturn("500.00");
        when(priceObj.getCurrency()).thenReturn("USD");
        when(offer.getPrice()).thenReturn(priceObj);

        FlightPrice.Itinerary itinerary = mock(FlightPrice.Itinerary.class);
        FlightPrice.Segment segment = mock(FlightPrice.Segment.class);
        FlightPrice.FlightEndpoint departure = mock(FlightPrice.FlightEndpoint.class);
        FlightPrice.FlightEndpoint arrival = mock(FlightPrice.FlightEndpoint.class);

        when(departure.getIataCode()).thenReturn("NYC");
        when(departure.getAt()).thenReturn("2025-12-01T10:00:00");
        when(arrival.getIataCode()).thenReturn("LAX");
        when(arrival.getAt()).thenReturn("2025-12-01T14:00:00");
        when(segment.getDeparture()).thenReturn(departure);
        when(segment.getArrival()).thenReturn(arrival);
        when(itinerary.getSegments()).thenReturn(new FlightPrice.Segment[]{segment});
        when(offer.getItineraries()).thenReturn(new FlightPrice.Itinerary[]{itinerary});

        flightOffers[0] = offer;
        when(price.getFlightOffers()).thenReturn(flightOffers);

        return price;
    }
}


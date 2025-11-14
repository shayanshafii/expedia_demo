package com.expedia.demo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.FlightPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlightDetailsService {
    private final Amadeus amadeus;

    @Autowired
    public FlightDetailsService(Amadeus amadeus) {
        this.amadeus = amadeus;
    }

    public Map<String, Object> getFlightDetails(String flightId) throws ResponseException {
        FlightOfferSearch[] offers = amadeus.shopping.flightOffersSearch.get(
                Params.with("id", flightId)
        );

        if (offers == null || offers.length == 0) {
            return null;
        }

        FlightOfferSearch offer = offers[0];
        FlightPrice price = amadeus.shopping.flightOffersSearch.pricing.post(offer);

        Map<String, Object> details = new HashMap<>();
        details.put("flight_id", flightId);
        details.put("price", price.getFlightOffers()[0].getPrice().getTotal());
        details.put("currency", price.getFlightOffers()[0].getPrice().getCurrency());

        if (price.getFlightOffers()[0].getItineraries() != null && price.getFlightOffers()[0].getItineraries().length > 0) {
            var itinerary = price.getFlightOffers()[0].getItineraries()[0];
            if (itinerary.getSegments() != null && itinerary.getSegments().length > 0) {
                var firstSegment = itinerary.getSegments()[0];
                var lastSegment = itinerary.getSegments()[itinerary.getSegments().length - 1];
                details.put("origin", firstSegment.getDeparture().getIataCode());
                details.put("destination", lastSegment.getArrival().getIataCode());
                details.put("departure_time", firstSegment.getDeparture().getAt());
                details.put("arrival_time", lastSegment.getArrival().getAt());
            }
        }

        return details;
    }
}


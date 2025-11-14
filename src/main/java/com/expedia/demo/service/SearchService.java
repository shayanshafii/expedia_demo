package com.expedia.demo.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.expedia.demo.model.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final Amadeus amadeus;

    @Autowired
    public SearchService(Amadeus amadeus) {
        this.amadeus = amadeus;
    }

    public List<Flight> searchFlights(String origin, String destination, String date) throws ResponseException {
        FlightOfferSearch[] offers = amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", origin)
                        .and("destinationLocationCode", destination)
                        .and("departureDate", date)
                        .and("adults", 1)
        );

        List<Flight> flights = new ArrayList<>();
        if (offers != null) {
            for (FlightOfferSearch offer : offers) {
                String flightId = offer.getId();
                String price = offer.getPrice().getTotal();
                String airline = offer.getValidatingAirlineCodes() != null && offer.getValidatingAirlineCodes().length > 0
                        ? offer.getValidatingAirlineCodes()[0] : "Unknown";

                String originCode = offer.getItineraries()[0].getSegments()[0].getDeparture().getIataCode();
                String destCode = offer.getItineraries()[0].getSegments()[offer.getItineraries()[0].getSegments().length - 1].getArrival().getIataCode();
                String depDate = offer.getItineraries()[0].getSegments()[0].getDeparture().getAt();

                Flight flight = new Flight(flightId, originCode, destCode, depDate, price, airline);
                flights.add(flight);
            }
        }
        return flights;
    }
}


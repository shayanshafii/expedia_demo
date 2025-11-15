package com.expedia.demo.service;

import com.expedia.demo.model.AviationstackRouteResponse;
import com.expedia.demo.model.Flight;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    @Autowired
    public SearchService(RestTemplate restTemplate,
                         @Value("${aviationstack.api.key}") String apiKey,
                         @Value("${aviationstack.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.objectMapper = new ObjectMapper();
    }

    public List<Flight> searchFlights(String origin, String destination, String date) {
        logger.info("SearchService.searchFlights called with parameters - origin: {}, destination: {}, date: {}", 
                origin, destination, date);
        
        String url = String.format("%s/routes?access_key=%s&dep_iata=%s&arr_iata=%s",
                apiUrl, apiKey, origin, destination);
        
        // Log URL with masked API key for security
        String maskedUrl = url.replace(apiKey, "***");
        logger.info("Making API request to URL: {}", maskedUrl);

        try {
            AviationstackRouteResponse response = restTemplate.getForObject(url, AviationstackRouteResponse.class);
            
            // Log the API response
            if (response != null) {
                try {
                    String responseJson = objectMapper.writeValueAsString(response);
                    logger.debug("API response received: {}", responseJson);
                } catch (Exception jsonException) {
                    logger.debug("API response received (unable to serialize to JSON): {}", response);
                }
            } else {
                logger.warn("API response is null");
            }
            
            List<Flight> flights = new ArrayList<>();

            if (response != null && response.getData() != null) {
                logger.info("Processing {} routes from API response", response.getData().size());
                for (AviationstackRouteResponse.Route route : response.getData()) {
                    String depIata = route.getDeparture() != null ? route.getDeparture().getIata() : origin;
                    String arrIata = route.getArrival() != null ? route.getArrival().getIata() : destination;
                    String airlineName = route.getAirline() != null && route.getAirline().getName() != null
                            ? route.getAirline().getName() : "Unknown";
                    String airlineIata = route.getAirline() != null && route.getAirline().getIata() != null
                            ? route.getAirline().getIata() : "";

                    // Generate flight_id from route data
                    String flightId = generateFlightId(depIata, arrIata, airlineIata);

                    Flight flight = new Flight(flightId, depIata, arrIata, date, airlineName);
                    flights.add(flight);
                }
            } else {
                logger.warn("API response data is null or empty");
            }

            logger.info("Returning {} flights", flights.size());
            return flights;
        } catch (Exception e) {
            logger.error("Error occurred while calling flight search API", e);
            return new ArrayList<>();
        }
    }

    private String generateFlightId(String origin, String destination, String airline) {
        try {
            String input = origin + destination + airline;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf((origin + destination + airline).hashCode());
        }
    }
}

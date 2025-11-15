package com.expedia.demo.service;

import com.expedia.demo.model.Flight;
import com.expedia.demo.model.FlightDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final ObjectMapper objectMapper;
    private List<Flight> allFlights;
    
    private static final DateTimeFormatter JSON_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter FRONTEND_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SearchService() {
        this.objectMapper = new ObjectMapper();
        this.allFlights = new ArrayList<>();
    }

    @PostConstruct
    public void loadFlights() {
        try {
            ClassPathResource resource = new ClassPathResource("flights.json");
            InputStream inputStream = resource.getInputStream();
            
            List<FlightDTO> flightDTOs = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<FlightDTO>>() {}
            );
            
            allFlights = flightDTOs.stream()
                .map(this::convertToFlight)
                .collect(Collectors.toList());
            
            logger.info("Loaded {} flights from flights.json", allFlights.size());
        } catch (Exception e) {
            logger.error("Error loading flights from flights.json", e);
            allFlights = new ArrayList<>();
        }
    }

    public List<Flight> searchFlights(String origin, String destination, String date) {
        logger.info("SearchService.searchFlights called with parameters - origin: {}, destination: {}, date: {}", 
                origin, destination, date);
        
        try {
            // Normalize date format for comparison
            String normalizedDate = normalizeDate(date);
            
            List<Flight> matchingFlights = allFlights.stream()
                .filter(flight -> 
                    flight.getOrigin().equalsIgnoreCase(origin) &&
                    flight.getDestination().equalsIgnoreCase(destination) &&
                    normalizeDate(flight.getDepartureDate()).equals(normalizedDate)
                )
                .collect(Collectors.toList());
            
            logger.info("Found {} matching flights for origin: {}, destination: {}, date: {}", 
                    matchingFlights.size(), origin, destination, date);
            
            return matchingFlights;
        } catch (Exception e) {
            logger.error("Error occurred while searching flights", e);
            return new ArrayList<>();
        }
    }
    
    private Flight convertToFlight(FlightDTO dto) {
        String flightId = generateFlightId(dto.getOrigin(), dto.getDestination(), dto.getAirline());
        return new Flight(flightId, dto.getOrigin(), dto.getDestination(), dto.getDate(), dto.getAirline());
    }
    
    private String normalizeDate(String date) {
        try {
            // Try to parse as frontend format (YYYY-MM-DD) first
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                LocalDate parsedDate = LocalDate.parse(date, FRONTEND_DATE_FORMAT);
                return parsedDate.format(JSON_DATE_FORMAT);
            }
            // If already in JSON format (MM/dd/yyyy), return as is
            if (date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return date;
            }
            // If parsing fails, return original date
            logger.warn("Unable to parse date format: {}", date);
            return date;
        } catch (Exception e) {
            logger.warn("Error normalizing date: {}", date, e);
            return date;
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

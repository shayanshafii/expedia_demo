package com.expedia.demo.storage;

import com.expedia.demo.model.Booking;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingStorage {
    private static final String BOOKINGS_FILE = "bookings.json";
    private final ObjectMapper objectMapper;
    private final Path bookingsPath;

    public BookingStorage() {
        this.objectMapper = new ObjectMapper();
        this.bookingsPath = Paths.get("src/main/resources", BOOKINGS_FILE);
        initializeFile();
    }

    private void initializeFile() {
        try {
            if (!Files.exists(bookingsPath)) {
                Files.createDirectories(bookingsPath.getParent());
                Files.createFile(bookingsPath);
                writeBookings(new ArrayList<>());
            }
        } catch (IOException e) {
            // Handle initialization error
        }
    }

    public List<Booking> readBookings() {
        try {
            File file = bookingsPath.toFile();
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Booking>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void writeBookings(List<Booking> bookings) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(bookingsPath.toFile(), bookings);
        } catch (IOException e) {
            // Handle write error
        }
    }

    public Optional<Booking> findBooking(String userId, String flightId) {
        List<Booking> bookings = readBookings();
        return bookings.stream()
                .filter(b -> b.getUserId().equals(userId) && b.getFlightId().equals(flightId))
                .findFirst();
    }

    public void saveBooking(Booking booking) {
        List<Booking> bookings = readBookings();
        bookings.add(booking);
        writeBookings(bookings);
    }

    public void updateBooking(Booking updatedBooking) {
        List<Booking> bookings = readBookings();
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            if (booking.getUserId().equals(updatedBooking.getUserId()) &&
                booking.getFlightId().equals(updatedBooking.getFlightId())) {
                bookings.set(i, updatedBooking);
                break;
            }
        }
        writeBookings(bookings);
    }
}


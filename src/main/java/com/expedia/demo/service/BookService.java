package com.expedia.demo.service;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.model.Booking;
import com.expedia.demo.storage.BookingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class BookService {
    private final BookingStorage bookingStorage;

    @Autowired
    public BookService(BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    public BookResponse createBooking(BookRequest request) {
        String userId = generateUserId(request.getPassengerName(), request.getPassengerEmail());
        String flightId = request.getFlightId();
        String status = "PENDING";
        String amount = "0.00";
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Optional<Booking> existingBooking = bookingStorage.findBooking(userId, flightId);
        if (existingBooking.isPresent()) {
            Booking booking = existingBooking.get();
            return new BookResponse(booking.getUserId(), booking.getFlightId(), booking.getStatus(), "completed!");
        }

        Booking booking = new Booking(userId, flightId, request.getPassengerName(), request.getPassengerEmail(), status, amount, createdAt);
        bookingStorage.saveBooking(booking);

        return new BookResponse(userId, flightId, status, "completed!");
    }

    private String generateUserId(String name, String email) {
        try {
            String input = name + email;
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
            return String.valueOf((name + email).hashCode());
        }
    }
}


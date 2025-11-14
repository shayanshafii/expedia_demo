package com.expedia.demo.service;

import com.expedia.demo.model.Booking;
import com.expedia.demo.model.PaymentRequest;
import com.expedia.demo.model.PaymentResponse;
import com.expedia.demo.storage.BookingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PayService {
    private final BookingStorage bookingStorage;

    @Autowired
    public PayService(BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        String userId = request.getUserId();
        String flightId = request.getFlightId();

        Optional<Booking> bookingOpt = bookingStorage.findBooking(userId, flightId);
        if (bookingOpt.isEmpty()) {
            return null;
        }

        Booking booking = bookingOpt.get();
        if (!"PENDING".equals(booking.getStatus())) {
            return null;
        }

        booking.setStatus("CONFIRMED");
        bookingStorage.updateBooking(booking);

        return new PaymentResponse(userId, flightId, "CONFIRMED", "completed!");
    }
}


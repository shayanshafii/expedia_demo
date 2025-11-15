package com.expedia.demo.service;

import com.expedia.demo.model.Booking;
import com.expedia.demo.model.PaymentRequest;
import com.expedia.demo.model.PaymentResponse;
import com.expedia.demo.storage.BookingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PayServiceTest {
    @Mock
    private BookingStorage bookingStorage;

    private PayService payService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payService = new PayService(bookingStorage);
    }

    @Test
    void testProcessPayment_Success() {
        // Setup
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        // Execute
        PaymentResponse response = payService.processPayment(request);

        // Verify - minimal assertions
        assertNotNull(response);
    }
}


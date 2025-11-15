package com.expedia.demo.service;

import com.expedia.demo.model.Booking;
import com.expedia.demo.model.PaymentRequest;
import com.expedia.demo.model.PaymentResponse;
import com.expedia.demo.storage.BookingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

        // Verify - enhanced assertions
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());

        // Verify booking status was updated
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).updateBooking(bookingCaptor.capture());
        
        Booking updatedBooking = bookingCaptor.getValue();
        assertEquals("CONFIRMED", updatedBooking.getStatus());
        assertEquals(userId, updatedBooking.getUserId());
        assertEquals(flightId, updatedBooking.getFlightId());
    }

    @Test
    void testProcessPayment_BookingNotFound() {
        // Setup
        String userId = "non-existent-user";
        String flightId = "non-existent-flight";

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.empty());

        // Execute
        PaymentResponse response = payService.processPayment(request);

        // Verify - should return null
        assertNull(response);

        // Verify updateBooking was NOT called
        verify(bookingStorage, never()).updateBooking(any(Booking.class));
    }

    @Test
    void testProcessPayment_AlreadyConfirmed() {
        // Setup
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("CONFIRMED");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        // Execute
        PaymentResponse response = payService.processPayment(request);

        // Verify - should return null (not PENDING)
        assertNull(response);

        // Verify updateBooking was NOT called
        verify(bookingStorage, never()).updateBooking(any(Booking.class));
    }

    @Test
    void testProcessPayment_StatusTransition() {
        // Setup
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setPassengerName("John Doe");
        booking.setPassengerEmail("john@example.com");
        booking.setStatus("PENDING");
        booking.setCreatedAt("2025-01-01T10:00:00");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("500.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        // Execute
        PaymentResponse response = payService.processPayment(request);

        // Verify - status should transition from PENDING to CONFIRMED
        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());

        // Verify the booking object was updated correctly
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).updateBooking(bookingCaptor.capture());
        
        Booking updatedBooking = bookingCaptor.getValue();
        assertEquals("CONFIRMED", updatedBooking.getStatus());
        assertEquals("John Doe", updatedBooking.getPassengerName());
        assertEquals("john@example.com", updatedBooking.getPassengerEmail());
    }
}


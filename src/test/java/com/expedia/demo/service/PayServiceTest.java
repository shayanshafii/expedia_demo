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

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
    }

    @Test
    void testProcessPayment_BookingNotFound() {
        String userId = "nonexistent-user";
        String flightId = "nonexistent-flight";

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("500.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.empty());

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any());
    }

    @Test
    void testProcessPayment_BookingAlreadyConfirmed() {
        String userId = "user-123";
        String flightId = "flight-456";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("CONFIRMED");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("300.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any());
    }

    @Test
    void testProcessPayment_UpdatesBookingStatus() {
        String userId = "user-789";
        String flightId = "flight-101";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("PENDING");
        booking.setPassengerName("Test Passenger");
        booking.setPassengerEmail("test@example.com");
        booking.setCreatedAt("2025-01-15T10:00:00");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("debit_card");
        request.setAmount("450.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).updateBooking(bookingCaptor.capture());

        Booking updatedBooking = bookingCaptor.getValue();
        assertNotNull(updatedBooking);
        assertEquals("CONFIRMED", updatedBooking.getStatus());
        assertEquals(userId, updatedBooking.getUserId());
        assertEquals(flightId, updatedBooking.getFlightId());

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testProcessPayment_VerifyResponseFields() {
        String userId = "user-abc";
        String flightId = "flight-xyz";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("paypal");
        request.setAmount("250.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testProcessPayment_NonPendingStatus_ReturnsNull() {
        String userId = "user-def";
        String flightId = "flight-ghi";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("CANCELLED");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("100.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any());
    }
}


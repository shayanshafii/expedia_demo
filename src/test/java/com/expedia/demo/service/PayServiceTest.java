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
        assertEquals(userId, response.getUserId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testProcessPayment_BookingNotFound_ReturnsNull() {
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("100.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.empty());

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any());
    }

    @Test
    void testProcessPayment_BookingNotPending_ReturnsNull() {
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
        request.setAmount("100.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any());
    }

    @Test
    void testProcessPayment_UpdatesStatusToConfirmed() {
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
        request.setAmount("100.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        payService.processPayment(request);

        verify(bookingStorage).updateBooking(bookingCaptor.capture());
        Booking updatedBooking = bookingCaptor.getValue();
        assertEquals("CONFIRMED", updatedBooking.getStatus());
        assertEquals(userId, updatedBooking.getUserId());
        assertEquals(flightId, updatedBooking.getFlightId());
    }
}


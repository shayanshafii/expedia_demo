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
        request.setAmount("299.99");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());
        
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).updateBooking(bookingCaptor.capture());
        assertEquals("CONFIRMED", bookingCaptor.getValue().getStatus());
    }

    @Test
    void testProcessPayment_BookingNotFound() {
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("299.99");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.empty());

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any(Booking.class));
    }

    @Test
    void testProcessPayment_AlreadyConfirmed() {
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
        request.setAmount("299.99");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
        verify(bookingStorage, never()).updateBooking(any(Booking.class));
    }

    @Test
    void testProcessPayment_WithDebitCard() {
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("debit_card");
        request.setAmount("150.00");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
        verify(bookingStorage, times(1)).updateBooking(any(Booking.class));
    }

    @Test
    void testProcessPayment_WithPayPal() {
        String userId = "test-user-id";
        String flightId = "test-flight-id";

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setPaymentMethod("paypal");
        request.setAmount("450.75");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void testProcessPayment_LargeAmount() {
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
        request.setAmount("9999.99");

        when(bookingStorage.findBooking(userId, flightId)).thenReturn(Optional.of(booking));

        PaymentResponse response = payService.processPayment(request);

        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void testProcessPayment_DifferentUserIds() {
        String userId1 = "user-id-1";
        String userId2 = "user-id-2";
        String flightId = "test-flight-id";

        Booking booking1 = new Booking();
        booking1.setUserId(userId1);
        booking1.setFlightId(flightId);
        booking1.setStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.setUserId(userId2);
        request.setFlightId(flightId);
        request.setPaymentMethod("credit_card");
        request.setAmount("299.99");

        when(bookingStorage.findBooking(userId2, flightId)).thenReturn(Optional.empty());

        PaymentResponse response = payService.processPayment(request);

        assertNull(response);
    }

    @Test
    void testProcessPayment_EmptyAmount() {
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
        assertEquals("CONFIRMED", response.getStatus());
    }
}


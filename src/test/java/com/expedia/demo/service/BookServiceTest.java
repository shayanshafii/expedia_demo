package com.expedia.demo.service;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.model.Booking;
import com.expedia.demo.storage.BookingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    @Mock
    private BookingStorage bookingStorage;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookingStorage);
    }

    @Test
    void testCreateBooking_Success() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());
        when(bookingStorage.readBookings()).thenReturn(new ArrayList<>());

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("completed!", response.getMessage());
        verify(bookingStorage, times(1)).saveBooking(any(Booking.class));
    }

    @Test
    void testCreateBooking_DuplicateBooking() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        Booking existingBooking = new Booking();
        existingBooking.setUserId("existing-user-id");
        existingBooking.setFlightId("test-flight-id");
        existingBooking.setStatus("PENDING");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.of(existingBooking));

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertEquals("existing-user-id", response.getUserId());
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        verify(bookingStorage, never()).saveBooking(any(Booking.class));
    }

    @Test
    void testCreateBooking_DifferentPassengersSameEmail() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("John Doe");
        request1.setPassengerEmail("john@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("Jane Doe");
        request2.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertNotEquals(response1.getUserId(), response2.getUserId());
    }

    @Test
    void testCreateBooking_SamePassengerDifferentFlights() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("John Doe");
        request1.setPassengerEmail("john@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("John Doe");
        request2.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(response1.getUserId(), response2.getUserId());
        assertNotEquals(response1.getFlightId(), response2.getFlightId());
    }

    @Test
    void testCreateBooking_WithSpecialCharacters() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("José García");
        request.setPassengerEmail("jose.garcia@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void testCreateBooking_WithLongNames() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("Christopher Alexander Montgomery Wellington III");
        request.setPassengerEmail("christopher.wellington@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals(16, response.getUserId().length());
    }

    @Test
    void testCreateBooking_ConsistentUserIdGeneration() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("John Doe");
        request1.setPassengerEmail("john@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("John Doe");
        request2.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        assertEquals(response1.getUserId(), response2.getUserId());
    }
}


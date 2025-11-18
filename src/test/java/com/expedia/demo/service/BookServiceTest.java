package com.expedia.demo.service;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.model.Booking;
import com.expedia.demo.storage.BookingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    }

    @Test
    void testCreateBooking_NewBooking_SavesCalled() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-123");
        request.setPassengerName("Jane Smith");
        request.setPassengerEmail("jane@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response = bookService.createBooking(request);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).saveBooking(bookingCaptor.capture());

        Booking capturedBooking = bookingCaptor.getValue();
        assertNotNull(capturedBooking);
        assertEquals(16, capturedBooking.getUserId().length());
        assertEquals("flight-123", capturedBooking.getFlightId());
        assertEquals("Jane Smith", capturedBooking.getPassengerName());
        assertEquals("jane@example.com", capturedBooking.getPassengerEmail());
        assertEquals("PENDING", capturedBooking.getStatus());
        assertNotNull(capturedBooking.getCreatedAt());
        assertTrue(capturedBooking.getCreatedAt().matches("\\d{4}-\\d{2}-\\d{2}T.*"));

        assertNotNull(response);
        assertEquals(capturedBooking.getUserId(), response.getUserId());
        assertEquals("flight-123", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testCreateBooking_DuplicateBooking() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-456");
        request.setPassengerName("Bob Jones");
        request.setPassengerEmail("bob@example.com");

        Booking existingBooking = new Booking();
        existingBooking.setUserId("existing-user-id");
        existingBooking.setFlightId("flight-456");
        existingBooking.setPassengerName("Bob Jones");
        existingBooking.setPassengerEmail("bob@example.com");
        existingBooking.setStatus("CONFIRMED");
        existingBooking.setCreatedAt("2025-01-15T10:30:00");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.of(existingBooking));

        BookResponse response = bookService.createBooking(request);

        verify(bookingStorage, never()).saveBooking(any());

        assertNotNull(response);
        assertEquals("existing-user-id", response.getUserId());
        assertEquals("flight-456", response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testCreateBooking_DifferentPassengers_DifferentUserIds() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-789");
        request1.setPassengerName("Alice Brown");
        request1.setPassengerEmail("alice@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-789");
        request2.setPassengerName("Charlie Davis");
        request2.setPassengerEmail("charlie@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertNotEquals(response1.getUserId(), response2.getUserId());
    }

    @Test
    void testCreateBooking_SamePassenger_SameUserId() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-100");
        request1.setPassengerName("David Wilson");
        request1.setPassengerEmail("david@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-200");
        request2.setPassengerName("David Wilson");
        request2.setPassengerEmail("david@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(response1.getUserId(), response2.getUserId());
    }

    @Test
    void testCreateBooking_UserIdIsHexadecimal() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-hex");
        request.setPassengerName("Test User");
        request.setPassengerEmail("test@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals(16, response.getUserId().length());
        assertTrue(response.getUserId().matches("[0-9a-f]{16}"));
    }
}


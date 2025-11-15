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
        assertNotNull(response.getUserId());
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("completed!", response.getMessage());
        verify(bookingStorage).saveBooking(any());
    }

    @Test
    void testCreateBooking_DuplicateBooking_ReturnsExisting() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        Booking existingBooking = new Booking();
        existingBooking.setUserId("existing-user-id");
        existingBooking.setFlightId("test-flight-id");
        existingBooking.setStatus("CONFIRMED");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.of(existingBooking));

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertEquals("existing-user-id", response.getUserId());
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        verify(bookingStorage, never()).saveBooking(any());
    }

    @Test
    void testCreateBooking_PopulatesAllResponseFields() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-123");
        request.setPassengerName("Jane Smith");
        request.setPassengerEmail("jane@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        BookResponse response = bookService.createBooking(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertFalse(response.getUserId().isEmpty());
        assertEquals("flight-123", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("completed!", response.getMessage());
    }

    @Test
    void testGenerateUserId_Deterministic() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("John Doe");
        request1.setPassengerEmail("john@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("John Doe");
        request2.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        bookService.createBooking(request1);
        verify(bookingStorage).saveBooking(bookingCaptor.capture());
        String userId1 = bookingCaptor.getValue().getUserId();

        reset(bookingStorage);
        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        bookService.createBooking(request2);
        verify(bookingStorage).saveBooking(bookingCaptor.capture());
        String userId2 = bookingCaptor.getValue().getUserId();

        assertEquals(userId1, userId2);
    }

    @Test
    void testCreateBooking_VerifiesStatusPending() {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("Test User");
        request.setPassengerEmail("test@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        bookService.createBooking(request);

        verify(bookingStorage).saveBooking(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals("PENDING", savedBooking.getStatus());
        assertNotNull(savedBooking.getCreatedAt());
    }
}


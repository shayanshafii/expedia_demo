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
        // Setup
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        // Execute
        BookResponse response = bookService.createBooking(request);

        // Verify - enhanced assertions
        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals(16, response.getUserId().length());
        assertTrue(response.getUserId().matches("[0-9a-f]{16}"));
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("completed!", response.getMessage());

        // Verify saveBooking was called
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingStorage, times(1)).saveBooking(bookingCaptor.capture());
        
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals(response.getUserId(), savedBooking.getUserId());
        assertEquals("test-flight-id", savedBooking.getFlightId());
        assertEquals("John Doe", savedBooking.getPassengerName());
        assertEquals("john@example.com", savedBooking.getPassengerEmail());
        assertEquals("PENDING", savedBooking.getStatus());
    }

    @Test
    void testCreateBooking_DuplicateBooking() {
        // Setup
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        Booking existingBooking = new Booking();
        existingBooking.setUserId("existing-user-id");
        existingBooking.setFlightId("test-flight-id");
        existingBooking.setStatus("CONFIRMED");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.of(existingBooking));

        // Execute
        BookResponse response = bookService.createBooking(request);

        // Verify - should return existing booking info
        assertNotNull(response);
        assertEquals("existing-user-id", response.getUserId());
        assertEquals("test-flight-id", response.getFlightId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals("completed!", response.getMessage());

        // Verify saveBooking was NOT called
        verify(bookingStorage, never()).saveBooking(any(Booking.class));
    }

    @Test
    void testCreateBooking_UserIdDeterminism() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("Jane Smith");
        request1.setPassengerEmail("jane@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("Jane Smith");
        request2.setPassengerEmail("jane@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        // Execute
        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        // Verify - same name/email should generate same userId
        assertEquals(response1.getUserId(), response2.getUserId());

        BookRequest request3 = new BookRequest();
        request3.setFlightId("flight-3");
        request3.setPassengerName("Jane Smith");
        request3.setPassengerEmail("jane.different@example.com");

        // Execute
        BookResponse response3 = bookService.createBooking(request3);

        // Verify - different email should generate different userId
        assertNotEquals(response1.getUserId(), response3.getUserId());
    }

    @Test
    void testCreateBooking_DifferentNameGeneratesDifferentUserId() {
        // Setup
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("Alice");
        request1.setPassengerEmail("test@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("Bob");
        request2.setPassengerEmail("test@example.com");

        when(bookingStorage.findBooking(anyString(), anyString())).thenReturn(Optional.empty());

        // Execute
        BookResponse response1 = bookService.createBooking(request1);
        BookResponse response2 = bookService.createBooking(request2);

        // Verify - different names should generate different userIds
        assertNotEquals(response1.getUserId(), response2.getUserId());
    }
}


package com.expedia.demo.service;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.storage.BookingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        when(bookingStorage.readBookings()).thenReturn(new ArrayList<>());

        // Execute
        BookResponse response = bookService.createBooking(request);

        // Verify - minimal assertions
        assertNotNull(response);
    }
}


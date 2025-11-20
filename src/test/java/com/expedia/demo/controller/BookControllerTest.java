package com.expedia.demo.controller;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {
    @Mock
    private BookService bookService;

    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookController = new BookController(bookService);
    }

    @Test
    void testBook_Success() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-123");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        BookResponse mockResponse = new BookResponse("user-123", "flight-123", "PENDING", "completed!");

        when(bookService.createBooking(request)).thenReturn(mockResponse);

        ResponseEntity<BookResponse> response = bookController.book(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("user-123", response.getBody().getUserId());
        assertEquals("flight-123", response.getBody().getFlightId());
        assertEquals("PENDING", response.getBody().getStatus());
        verify(bookService, times(1)).createBooking(request);
    }

    @Test
    void testBook_DuplicateBooking() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-123");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        BookResponse mockResponse = new BookResponse("user-123", "flight-123", "CONFIRMED", "completed!");

        when(bookService.createBooking(request)).thenReturn(mockResponse);

        ResponseEntity<BookResponse> response = bookController.book(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CONFIRMED", response.getBody().getStatus());
    }

    @Test
    void testBook_WithSpecialCharacters() {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-456");
        request.setPassengerName("José García");
        request.setPassengerEmail("jose.garcia@example.com");

        BookResponse mockResponse = new BookResponse("user-456", "flight-456", "PENDING", "completed!");

        when(bookService.createBooking(request)).thenReturn(mockResponse);

        ResponseEntity<BookResponse> response = bookController.book(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user-456", response.getBody().getUserId());
    }

    @Test
    void testBook_MultipleBookings() {
        BookRequest request1 = new BookRequest();
        request1.setFlightId("flight-1");
        request1.setPassengerName("John Doe");
        request1.setPassengerEmail("john@example.com");

        BookRequest request2 = new BookRequest();
        request2.setFlightId("flight-2");
        request2.setPassengerName("Jane Smith");
        request2.setPassengerEmail("jane@example.com");

        BookResponse mockResponse1 = new BookResponse("user-1", "flight-1", "PENDING", "completed!");
        BookResponse mockResponse2 = new BookResponse("user-2", "flight-2", "PENDING", "completed!");

        when(bookService.createBooking(request1)).thenReturn(mockResponse1);
        when(bookService.createBooking(request2)).thenReturn(mockResponse2);

        ResponseEntity<BookResponse> response1 = bookController.book(request1);
        ResponseEntity<BookResponse> response2 = bookController.book(request2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals("user-1", response1.getBody().getUserId());
        assertEquals("user-2", response2.getBody().getUserId());
    }
}

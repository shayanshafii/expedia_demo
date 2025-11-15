package com.expedia.demo.controller;

import com.expedia.demo.model.BookRequest;
import com.expedia.demo.model.BookResponse;
import com.expedia.demo.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void testBook_ReturnsOkWithResponse() throws Exception {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-123");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        BookResponse response = new BookResponse("user-123", "flight-123", "PENDING", "completed!");

        when(bookService.createBooking(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user-123"))
                .andExpect(jsonPath("$.flight_id").value("flight-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("completed!"));

        verify(bookService).createBooking(any(BookRequest.class));
    }

    @Test
    void testBook_CallsServiceWithRequestBody() throws Exception {
        BookRequest request = new BookRequest();
        request.setFlightId("flight-456");
        request.setPassengerName("Jane Smith");
        request.setPassengerEmail("jane@example.com");

        BookResponse response = new BookResponse("user-456", "flight-456", "PENDING", "completed!");

        when(bookService.createBooking(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService).createBooking(any(BookRequest.class));
    }

    @Test
    void testBook_MapsSnakeCaseFields() throws Exception {
        String jsonRequest = "{\"flight_id\":\"flight-789\",\"passenger_name\":\"Bob Johnson\",\"passenger_email\":\"bob@example.com\"}";

        BookResponse response = new BookResponse("user-789", "flight-789", "PENDING", "completed!");

        when(bookService.createBooking(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user-789"))
                .andExpect(jsonPath("$.flight_id").value("flight-789"));
    }
}

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
    void testBook_ValidRequest() throws Exception {
        BookRequest request = new BookRequest();
        request.setFlightId("test-flight-id");
        request.setPassengerName("John Doe");
        request.setPassengerEmail("john@example.com");

        BookResponse mockResponse = new BookResponse(
            "abc123def456",
            "test-flight-id",
            "PENDING",
            "completed!"
        );

        when(bookService.createBooking(any(BookRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user_id").value("abc123def456"))
            .andExpect(jsonPath("$.flight_id").value("test-flight-id"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.message").value("completed!"));

        verify(bookService, times(1)).createBooking(any(BookRequest.class));
    }

    @Test
    void testBook_SnakeCaseJsonMapping() throws Exception {
        String jsonRequest = "{\"flight_id\":\"flight-123\",\"passenger_name\":\"Jane Smith\",\"passenger_email\":\"jane@example.com\"}";

        BookResponse mockResponse = new BookResponse(
            "user123",
            "flight-123",
            "PENDING",
            "completed!"
        );

        when(bookService.createBooking(any(BookRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user_id").value("user123"))
            .andExpect(jsonPath("$.flight_id").value("flight-123"));

        verify(bookService, times(1)).createBooking(any(BookRequest.class));
    }

    @Test
    void testBook_MalformedJson() throws Exception {
        String malformedJson = "{\"flight_id\":\"test\", invalid json}";

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());

        verify(bookService, never()).createBooking(any(BookRequest.class));
    }

    @Test
    void testBook_EmptyBody() throws Exception {
        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());

        verify(bookService, never()).createBooking(any(BookRequest.class));
    }
}

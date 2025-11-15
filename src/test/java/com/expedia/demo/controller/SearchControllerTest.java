package com.expedia.demo.controller;

import com.expedia.demo.model.Flight;
import com.expedia.demo.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    void testSearch_ValidParams() throws Exception {
        List<Flight> mockFlights = Arrays.asList(
            new Flight("flight-1", "NYC", "LAX", "12/01/2025", "Test Airline"),
            new Flight("flight-2", "NYC", "LAX", "12/01/2025", "Another Airline")
        );

        when(searchService.searchFlights("NYC", "LAX", "2025-12-01"))
            .thenReturn(mockFlights);

        mockMvc.perform(get("/api/search")
                .param("origin", "NYC")
                .param("destination", "LAX")
                .param("date", "2025-12-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].origin").value("NYC"))
            .andExpect(jsonPath("$[0].destination").value("LAX"))
            .andExpect(jsonPath("$[0].airline").value("Test Airline"));

        verify(searchService, times(1)).searchFlights("NYC", "LAX", "2025-12-01");
    }

    @Test
    void testSearch_MissingOriginParam() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("destination", "LAX")
                .param("date", "2025-12-01"))
            .andExpect(status().isBadRequest());

        verify(searchService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearch_MissingDestinationParam() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("origin", "NYC")
                .param("date", "2025-12-01"))
            .andExpect(status().isBadRequest());

        verify(searchService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearch_MissingDateParam() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("origin", "NYC")
                .param("destination", "LAX"))
            .andExpect(status().isBadRequest());

        verify(searchService, never()).searchFlights(anyString(), anyString(), anyString());
    }

    @Test
    void testSearch_EmptyResults() throws Exception {
        when(searchService.searchFlights("ATL", "MIA", "2025-12-01"))
            .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/search")
                .param("origin", "ATL")
                .param("destination", "MIA")
                .param("date", "2025-12-01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(searchService, times(1)).searchFlights("ATL", "MIA", "2025-12-01");
    }
}

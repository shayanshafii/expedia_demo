package com.expedia.demo.controller;

import com.expedia.demo.model.Flight;
import com.expedia.demo.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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
    void testSearch_ReturnsOkWithFlights() throws Exception {
        List<Flight> flights = new ArrayList<>();
        flights.add(new Flight("flight-1", "NYC", "LAX", "12/01/2025", "Test Airline"));
        flights.add(new Flight("flight-2", "NYC", "LAX", "12/01/2025", "United"));

        when(searchService.searchFlights("NYC", "LAX", "2025-12-01")).thenReturn(flights);

        mockMvc.perform(get("/api/search")
                .param("origin", "NYC")
                .param("destination", "LAX")
                .param("date", "2025-12-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].origin").value("NYC"))
                .andExpect(jsonPath("$[0].destination").value("LAX"));

        verify(searchService).searchFlights("NYC", "LAX", "2025-12-01");
    }

    @Test
    void testSearch_CallsServiceWithCorrectParams() throws Exception {
        when(searchService.searchFlights("SFO", "ORD", "2025-12-15")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/search")
                .param("origin", "SFO")
                .param("destination", "ORD")
                .param("date", "2025-12-15"))
                .andExpect(status().isOk());

        verify(searchService).searchFlights("SFO", "ORD", "2025-12-15");
    }

    @Test
    void testSearch_EmptyResults_ReturnsEmptyList() throws Exception {
        when(searchService.searchFlights("ATL", "MIA", "2025-12-01")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/search")
                .param("origin", "ATL")
                .param("destination", "MIA")
                .param("date", "2025-12-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

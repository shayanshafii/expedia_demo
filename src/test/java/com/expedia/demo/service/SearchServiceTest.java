package com.expedia.demo.service;

import com.amadeus.Amadeus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class SearchServiceTest {
    @Mock
    private Amadeus amadeus;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchService = new SearchService(amadeus);
    }

    @Test
    void testSearchServiceExists() {
        assertNotNull(searchService);
    }
}

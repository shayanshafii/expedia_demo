package com.expedia.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RestTemplateConfigTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testRestTemplateBean_Loads() {
        assertNotNull(restTemplate);
    }

    @Test
    void testRestTemplateBean_IsConfigured() {
        assertNotNull(restTemplate);
        assertNotNull(restTemplate.getRequestFactory());
    }
}

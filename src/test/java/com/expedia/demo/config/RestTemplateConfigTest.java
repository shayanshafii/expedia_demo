package com.expedia.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class RestTemplateConfigTest {
    @Test
    void testRestTemplate_BeanCreation() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();
        
        assertNotNull(restTemplate);
    }
}

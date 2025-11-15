package com.expedia.demo.controller;

import com.expedia.demo.model.PaymentRequest;
import com.expedia.demo.model.PaymentResponse;
import com.expedia.demo.service.PayService;
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

@WebMvcTest(PayController.class)
public class PayControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PayService payService;

    @Test
    void testPay_Success() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("test-user-id");
        request.setFlightId("test-flight-id");
        request.setPaymentMethod("credit_card");
        request.setAmount("500.00");

        PaymentResponse mockResponse = new PaymentResponse(
            "test-user-id",
            "test-flight-id",
            "CONFIRMED",
            "completed!"
        );

        when(payService.processPayment(any(PaymentRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user_id").value("test-user-id"))
            .andExpect(jsonPath("$.flight_id").value("test-flight-id"))
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.message").value("completed!"));

        verify(payService, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testPay_BookingNotFound() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("non-existent-user");
        request.setFlightId("non-existent-flight");
        request.setPaymentMethod("credit_card");
        request.setAmount("500.00");

        when(payService.processPayment(any(PaymentRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(payService, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testPay_MalformedJson() throws Exception {
        String malformedJson = "{\"user_id\":\"test\", invalid json}";

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());

        verify(payService, never()).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testPay_EmptyBody() throws Exception {
        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());

        verify(payService, never()).processPayment(any(PaymentRequest.class));
    }
}

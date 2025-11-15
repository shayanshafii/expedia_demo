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
    void testPay_Success_ReturnsOk() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-123");
        request.setFlightId("flight-123");
        request.setPaymentMethod("credit_card");
        request.setAmount("350.00");

        PaymentResponse response = new PaymentResponse("user-123", "flight-123", "CONFIRMED", "completed!");

        when(payService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user-123"))
                .andExpect(jsonPath("$.flight_id").value("flight-123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.message").value("completed!"));

        verify(payService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testPay_ServiceReturnsNull_ReturnsBadRequest() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-456");
        request.setFlightId("flight-456");
        request.setPaymentMethod("credit_card");
        request.setAmount("280.00");

        when(payService.processPayment(any(PaymentRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(payService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testPay_CallsServiceWithRequestBody() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-789");
        request.setFlightId("flight-789");
        request.setPaymentMethod("debit_card");
        request.setAmount("400.00");

        PaymentResponse response = new PaymentResponse("user-789", "flight-789", "CONFIRMED", "completed!");

        when(payService.processPayment(any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(payService).processPayment(any(PaymentRequest.class));
    }
}

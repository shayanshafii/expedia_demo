package com.expedia.demo.controller;

import com.expedia.demo.model.PaymentRequest;
import com.expedia.demo.model.PaymentResponse;
import com.expedia.demo.service.PayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PayControllerTest {
    @Mock
    private PayService payService;

    private PayController payController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        payController = new PayController(payService);
    }

    @Test
    void testPay_Success() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-123");
        request.setFlightId("flight-123");
        request.setPaymentMethod("credit_card");
        request.setAmount("299.99");

        PaymentResponse mockResponse = new PaymentResponse("user-123", "flight-123", "CONFIRMED", "completed!");

        when(payService.processPayment(request)).thenReturn(mockResponse);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("user-123", response.getBody().getUserId());
        assertEquals("flight-123", response.getBody().getFlightId());
        assertEquals("CONFIRMED", response.getBody().getStatus());
        verify(payService, times(1)).processPayment(request);
    }

    @Test
    void testPay_BookingNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-123");
        request.setFlightId("flight-123");
        request.setPaymentMethod("credit_card");
        request.setAmount("299.99");

        when(payService.processPayment(request)).thenReturn(null);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(payService, times(1)).processPayment(request);
    }

    @Test
    void testPay_AlreadyConfirmed() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-123");
        request.setFlightId("flight-123");
        request.setPaymentMethod("credit_card");
        request.setAmount("299.99");

        when(payService.processPayment(request)).thenReturn(null);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testPay_WithDebitCard() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-456");
        request.setFlightId("flight-456");
        request.setPaymentMethod("debit_card");
        request.setAmount("150.00");

        PaymentResponse mockResponse = new PaymentResponse("user-456", "flight-456", "CONFIRMED", "completed!");

        when(payService.processPayment(request)).thenReturn(mockResponse);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CONFIRMED", response.getBody().getStatus());
    }

    @Test
    void testPay_WithPayPal() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-789");
        request.setFlightId("flight-789");
        request.setPaymentMethod("paypal");
        request.setAmount("450.75");

        PaymentResponse mockResponse = new PaymentResponse("user-789", "flight-789", "CONFIRMED", "completed!");

        when(payService.processPayment(request)).thenReturn(mockResponse);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CONFIRMED", response.getBody().getStatus());
    }

    @Test
    void testPay_LargeAmount() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-999");
        request.setFlightId("flight-999");
        request.setPaymentMethod("credit_card");
        request.setAmount("9999.99");

        PaymentResponse mockResponse = new PaymentResponse("user-999", "flight-999", "CONFIRMED", "completed!");

        when(payService.processPayment(request)).thenReturn(mockResponse);

        ResponseEntity<PaymentResponse> response = payController.pay(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CONFIRMED", response.getBody().getStatus());
    }
}

package com.expedia.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("flight_id")
    private String flightId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("amount")
    private String amount;

    public PaymentRequest() {
    }

    public PaymentRequest(String userId, String flightId, String paymentMethod, String amount) {
        this.userId = userId;
        this.flightId = flightId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}


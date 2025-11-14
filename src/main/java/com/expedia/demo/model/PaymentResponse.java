package com.expedia.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentResponse {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("flight_id")
    private String flightId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public PaymentResponse() {
    }

    public PaymentResponse(String userId, String flightId, String status, String message) {
        this.userId = userId;
        this.flightId = flightId;
        this.status = status;
        this.message = message;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


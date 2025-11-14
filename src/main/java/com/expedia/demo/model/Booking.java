package com.expedia.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("flight_id")
    private String flightId;

    @JsonProperty("passenger_name")
    private String passengerName;

    @JsonProperty("passenger_email")
    private String passengerEmail;

    @JsonProperty("status")
    private String status;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("created_at")
    private String createdAt;

    public Booking() {
    }

    public Booking(String userId, String flightId, String passengerName, String passengerEmail, String status, String amount, String createdAt) {
        this.userId = userId;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
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

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}


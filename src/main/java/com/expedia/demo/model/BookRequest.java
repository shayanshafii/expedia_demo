package com.expedia.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookRequest {
    @JsonProperty("flight_id")
    private String flightId;

    @JsonProperty("passenger_name")
    private String passengerName;

    @JsonProperty("passenger_email")
    private String passengerEmail;

    public BookRequest() {
    }

    public BookRequest(String flightId, String passengerName, String passengerEmail) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
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
}


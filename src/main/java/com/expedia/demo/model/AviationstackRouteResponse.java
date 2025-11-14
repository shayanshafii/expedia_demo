package com.expedia.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AviationstackRouteResponse {
    @JsonProperty("data")
    private List<Route> data;

    public AviationstackRouteResponse() {
    }

    public List<Route> getData() {
        return data;
    }

    public void setData(List<Route> data) {
        this.data = data;
    }

    public static class Route {
        @JsonProperty("departure")
        private Departure departure;

        @JsonProperty("arrival")
        private Arrival arrival;

        @JsonProperty("airline")
        private Airline airline;

        public Route() {
        }

        public Departure getDeparture() {
            return departure;
        }

        public void setDeparture(Departure departure) {
            this.departure = departure;
        }

        public Arrival getArrival() {
            return arrival;
        }

        public void setArrival(Arrival arrival) {
            this.arrival = arrival;
        }

        public Airline getAirline() {
            return airline;
        }

        public void setAirline(Airline airline) {
            this.airline = airline;
        }
    }

    public static class Departure {
        @JsonProperty("iata")
        private String iata;

        @JsonProperty("airport")
        private String airport;

        public Departure() {
        }

        public String getIata() {
            return iata;
        }

        public void setIata(String iata) {
            this.iata = iata;
        }

        public String getAirport() {
            return airport;
        }

        public void setAirport(String airport) {
            this.airport = airport;
        }
    }

    public static class Arrival {
        @JsonProperty("iata")
        private String iata;

        @JsonProperty("airport")
        private String airport;

        public Arrival() {
        }

        public String getIata() {
            return iata;
        }

        public void setIata(String iata) {
            this.iata = iata;
        }

        public String getAirport() {
            return airport;
        }

        public void setAirport(String airport) {
            this.airport = airport;
        }
    }

    public static class Airline {
        @JsonProperty("name")
        private String name;

        @JsonProperty("iata")
        private String iata;

        public Airline() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIata() {
            return iata;
        }

        public void setIata(String iata) {
            this.iata = iata;
        }
    }
}


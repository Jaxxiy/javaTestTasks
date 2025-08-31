package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    @JsonProperty("origin")
    private String origin;

    @JsonProperty("origin_name")
    private String originName;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("destination_name")
    private String destinationName;

    @JsonProperty("departure_date")
    private String departureDate;

    @JsonProperty("departure_time")
    private String departureTime;

    @JsonProperty("arrival_date")
    private String arrivalDate;

    @JsonProperty("arrival_time")
    private String arrivalTime;

    @JsonProperty("carrier")
    private String carrier;

    @JsonProperty("stops")
    private int stops;

    @JsonProperty("price")
    private int price;

    public String getOrigin() { return origin; }
    public String getOriginName() { return originName; }
    public String getDestination() { return destination; }
    public String getDestinationName() { return destinationName; }
    public String getCarrier() { return carrier; }
    public int getPrice() { return price; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalDate() { return arrivalDate; }
    public String getArrivalTime() { return arrivalTime; }

    public long getFlightTime() {
        try {
            String normalizedDepTime = normalizeTime(departureTime);
            String normalizedArrTime = normalizeTime(arrivalTime);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            LocalDateTime departure = LocalDateTime.parse(
                    departureDate + " " + normalizedDepTime,
                    formatter
            );

            LocalDateTime arrival = LocalDateTime.parse(
                    arrivalDate + " " + normalizedArrTime,
                    formatter
            );

            return java.time.Duration.between(departure, arrival).toMinutes();

        } catch (Exception e) {
            System.out.println("Ошибка расчета времени для " + carrier +
                    ": " + departureDate + " " + departureTime +
                    " -> " + arrivalDate + " " + arrivalTime);
            return 0;
        }
    }

    private String normalizeTime(String time) {
        if (time == null) return "00:00";

        String[] parts = time.split(":");
        if (parts.length != 2) return time;

        String hours = parts[0];
        String minutes = parts[1];

        if (hours.length() == 1) {
            hours = "0" + hours;
        }

        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }

        return hours + ":" + minutes;
    }

    public boolean isVvoToTlv() {
        return "VVO".equals(origin) && "TLV".equals(destination);
    }
}
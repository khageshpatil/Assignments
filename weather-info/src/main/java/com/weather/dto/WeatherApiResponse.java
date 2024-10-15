package com.weather.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeatherApiResponse {

    private List<Weather> weather;
    private Main main;

    @Data
    public static class Weather {
        private String description; // Weather condition description, e.g., "clear sky"
    }

    @Data
    public static class Main {
        private double temp; // Temperature in Kelvin
    }
}

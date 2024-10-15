package com.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WeatherResponse {
    private String pincode;
    private LocalDate forDate;
    private String weatherDescription;
    private double temperature;
}

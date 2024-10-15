package com.weather.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pincode;
    private LocalDate forDate;
    private String weatherDescription;
    private double temperature;
    private LocalDateTime lastUpdated;
}

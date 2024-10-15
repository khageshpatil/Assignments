package com.weather.controller;

import com.weather.dto.WeatherResponse;
import com.weather.service.WeatherService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{pincode}")
    public WeatherResponse getWeather(
            @PathVariable String pincode,
            @RequestParam(value = "forDate", required = false) String dateStr) {
        // Validate the pincode format manually
        if (!isValidPincode(pincode)) {
            throw new IllegalArgumentException("Invalid pincode format. Pincode must be a 6-digit number.");
        }

        LocalDate forDate;
        try {
            forDate = (dateStr == null || dateStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format provided: {}", dateStr);
            throw new IllegalArgumentException("Invalid date format. Use 'yyyy-MM-dd'.");
        }

        logger.info("Fetching weather for pincode: {} on date: {}", pincode, forDate);
        return weatherService.getWeatherByPincodeAndDate(pincode, forDate);
    }

    // Method to validate pincode format
    private boolean isValidPincode(String pincode) {
        return pincode != null && pincode.matches("\\d{6}");
    }
}

package com.weather.service;

import com.weather.dto.WeatherResponse;
import com.weather.entity.PincodeEntity;
import com.weather.entity.WeatherEntity;
import com.weather.exception.ExternalServiceException;
import com.weather.repository.PincodeRepository;
import com.weather.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private ExternalWeatherService externalWeatherService;

    public WeatherResponse getWeatherByPincodeAndDate(String pincode, LocalDate forDate) {
        // To Check for cached weather data first
        WeatherEntity cachedWeather = weatherRepository.findByPincodeAndForDate(pincode, forDate);
        if (cachedWeather != null) {
            logger.info("Returning cached weather data for pincode: {} on date: {}", pincode, forDate);
            return new WeatherResponse(pincode, forDate, cachedWeather.getWeatherDescription(), cachedWeather.getTemperature());
        }

        // Checking for existing pincode entry
        PincodeEntity pincodeEntity = pincodeRepository.findById(pincode).orElse(null);
        if (pincodeEntity == null) {
            // If not found, fetch lat/long from external API
            pincodeEntity = externalWeatherService.getLatLongForPincode(pincode);
            
            // Save the new pincodeEntity into the database
            pincodeRepository.save(pincodeEntity);
            logger.info("Saved new pincode with lat/long: {} - lat: {}, lon: {}", pincode, pincodeEntity.getLatitude(), pincodeEntity.getLongitude());
        } else {
            logger.info("Found existing pincode with lat/long: {} - lat: {}, lon: {}", pincode, pincodeEntity.getLatitude(), pincodeEntity.getLongitude());
        }

        // Check if the provided date is today or a historical date
        WeatherResponse weatherResponse;
        if (forDate.isEqual(LocalDate.now())) {
            // Fetching the current weather data
            weatherResponse = externalWeatherService.getCurrentWeatherForLatLong(pincodeEntity.getLatitude(), pincodeEntity.getLongitude());
        } else {
            // Fetching historical weather data
            weatherResponse = externalWeatherService.getHistoricalWeatherForLatLong(pincodeEntity.getLatitude(), pincodeEntity.getLongitude(), forDate);
        }
        
        //To Check if the weather response is null and throw an exception
        if (weatherResponse == null) {
            throw new ExternalServiceException("Weather service returned no data for pincode: " + pincode);
        }


        // Save the weather data for future requests
        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setPincode(pincode);
        weatherEntity.setForDate(forDate);
        weatherEntity.setWeatherDescription(weatherResponse.getWeatherDescription());
        weatherEntity.setTemperature(weatherResponse.getTemperature());
        weatherEntity.setLastUpdated(LocalDateTime.now()); // Set the current timestamp
        weatherRepository.save(weatherEntity);

        logger.info("Weather data saved for pincode: {} on date: {}", pincode, forDate);
        return weatherResponse;
    }
}

package com.weather.service;

import com.weather.dto.GeocodingResponse;
import com.weather.dto.WeatherApiResponse;
import com.weather.dto.WeatherResponse;
import com.weather.entity.PincodeEntity;
import com.weather.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ExternalWeatherService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalWeatherService.class);

    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public PincodeEntity getLatLongForPincode(String pincode) {
        String geocodingUrl = "https://api.openweathermap.org/geo/1.0/zip?zip=" + pincode + ",IN&appid=" + apiKey;

        // Retry mechanism
        for (int i = 0; i < 3; i++) {
            try {
                GeocodingResponse geoResponse = restTemplate.getForObject(geocodingUrl, GeocodingResponse.class);
                if (geoResponse == null) {
                    throw new ExternalServiceException("No response from geocoding API for pincode: " + pincode);
                }
                return new PincodeEntity(pincode, geoResponse.getLat(), geoResponse.getLon(), LocalDateTime.now());
            } catch (Exception e) {
                logger.error("Error fetching lat/long for pincode {}. Retry {} of 3. Error: {}", pincode, i + 1, e.getMessage());
            }
        }
        throw new ExternalServiceException("Failed to fetch lat/long after 3 retries for pincode: " + pincode);
    }

    public WeatherResponse getCurrentWeatherForLatLong(double lat, double lon) {
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;

        // Retry mechanism for current weather API
        for (int i = 0; i < 3; i++) {
            try {
                WeatherApiResponse weatherApiResponse = restTemplate.getForObject(weatherUrl, WeatherApiResponse.class);
                if (weatherApiResponse == null || weatherApiResponse.getWeather() == null || weatherApiResponse.getWeather().isEmpty()) {
                    throw new ExternalServiceException("No response from weather API for lat: " + lat + " lon: " + lon);
                }
                return new WeatherResponse(
                        lat + "," + lon, // Temporary placeholder for pincode
                        LocalDate.now(),  // Returning current date for current weather
                        weatherApiResponse.getWeather().get(0).getDescription(),
                        weatherApiResponse.getMain().getTemp()
                );
            } catch (Exception e) {
                logger.error("Error fetching current weather data for lat: {}, lon: {}. Retry {} of 3. Error: {}", lat, lon, i + 1, e.getMessage());
            }
        }
        throw new ExternalServiceException("Failed to fetch current weather data after 3 retries for lat: " + lat + ", lon: " + lon);
    }

    public WeatherResponse getHistoricalWeatherForLatLong(double lat, double lon, LocalDate forDate) {
        //To convert LocalDate to a timestamp 
        long timestamp = forDate.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);

        String weatherUrl = "https://api.openweathermap.org/data/2.5/onecall/timemachine?lat=" + lat + "&lon=" + lon + "&dt=" + timestamp + "&appid=" + apiKey;

        // Retry mechanism for historical weather API
        for (int i = 0; i < 3; i++) {
            try {
                WeatherApiResponse weatherApiResponse = restTemplate.getForObject(weatherUrl, WeatherApiResponse.class);
                if (weatherApiResponse == null || weatherApiResponse.getWeather() == null || weatherApiResponse.getWeather().isEmpty()) {
                    throw new ExternalServiceException("No response from weather API for lat: " + lat + " lon: " + lon);
                }
                return new WeatherResponse(
                        lat + "," + lon, 
                        forDate,
                        weatherApiResponse.getWeather().get(0).getDescription(),
                        weatherApiResponse.getMain().getTemp()
                );
            } catch (Exception e) {
                logger.error("Error fetching historical weather data for lat: {}, lon: {}. Retry {} of 3. Error: {}", lat, lon, i + 1, e.getMessage());
            }
        }
        throw new ExternalServiceException("Failed to fetch historical weather data after 3 retries for lat: " + lat + ", lon: " + lon);
    }
}

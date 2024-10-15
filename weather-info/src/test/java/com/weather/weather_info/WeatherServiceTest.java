package com.weather.weather_info;

import com.weather.dto.WeatherResponse;
import com.weather.entity.PincodeEntity;
import com.weather.entity.WeatherEntity;
import com.weather.exception.ExternalServiceException;
import com.weather.repository.PincodeRepository;
import com.weather.repository.WeatherRepository;
import com.weather.service.ExternalWeatherService;
import com.weather.service.WeatherService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private PincodeRepository pincodeRepository;

    @Mock
    private ExternalWeatherService externalWeatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnCachedWeather_WhenWeatherExistsInRepository() {
        // Arrange
        String pincode = "411014";
        LocalDate forDate = LocalDate.of(2024, 10, 15);
        WeatherEntity cachedWeather = new WeatherEntity();
        cachedWeather.setWeatherDescription("Sunny");
        cachedWeather.setTemperature(30.0);

        when(weatherRepository.findByPincodeAndForDate(pincode, forDate)).thenReturn(cachedWeather);

        // Act
        WeatherResponse response = weatherService.getWeatherByPincodeAndDate(pincode, forDate);

        // Assert
        assertNotNull(response);
        assertEquals("Sunny", response.getWeatherDescription());
        assertEquals(30.0, response.getTemperature());
        verify(weatherRepository, times(1)).findByPincodeAndForDate(pincode, forDate);
        verifyNoMoreInteractions(externalWeatherService, pincodeRepository);
    }


    @Test
    void shouldThrowException_WhenExternalServiceReturnsNoData() {
        // Arrange
        String pincode = "411014";
        LocalDate forDate = LocalDate.of(2024, 10, 15);
        PincodeEntity pincodeEntity = new PincodeEntity(pincode, 18.5204, 73.8567,LocalDateTime.now());

        when(weatherRepository.findByPincodeAndForDate(pincode, forDate)).thenReturn(null);
        when(pincodeRepository.findById(pincode)).thenReturn(Optional.of(pincodeEntity));
        when(externalWeatherService.getHistoricalWeatherForLatLong(pincodeEntity.getLatitude(), pincodeEntity.getLongitude(), forDate))
                .thenReturn(null);

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> {
            weatherService.getWeatherByPincodeAndDate(pincode, forDate);
        });
    }
}

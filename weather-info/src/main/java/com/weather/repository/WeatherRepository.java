package com.weather.repository;

import com.weather.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {
    WeatherEntity findByPincodeAndForDate(String pincode, LocalDate forDate);
}

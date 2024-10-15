package com.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.weather")
public class WeatherInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherInfoApplication.class, args);
    }
}

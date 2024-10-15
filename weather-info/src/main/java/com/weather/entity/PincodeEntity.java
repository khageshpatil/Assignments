package com.weather.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PincodeEntity {
    @Id
    private String pincode;
    private double latitude;
    private double longitude;
    private LocalDateTime lastUpdated;
}

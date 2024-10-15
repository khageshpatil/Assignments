package com.weather.repository;

import com.weather.entity.PincodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PincodeRepository extends JpaRepository<PincodeEntity, String> {
}

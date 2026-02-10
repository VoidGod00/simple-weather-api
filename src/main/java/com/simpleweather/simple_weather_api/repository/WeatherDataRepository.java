package com.simpleweather.simple_weather_api.repository;
import com.simpleweather.simple_weather_api.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByPincodeAndForDate(String pincode, LocalDate forDate);
}

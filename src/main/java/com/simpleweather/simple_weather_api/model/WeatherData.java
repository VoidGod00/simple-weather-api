package com.simpleweather.simple_weather_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pincode;
    private LocalDate forDate;

    @Column(columnDefinition = "TEXT")
    private String weatherJson;
}

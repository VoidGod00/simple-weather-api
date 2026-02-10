package com.simpleweather.simple_weather_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String pincode;

    private Double latitude;
    private Double longitude;

    public Location(String pincode, Double latitude, Double longitude) {
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
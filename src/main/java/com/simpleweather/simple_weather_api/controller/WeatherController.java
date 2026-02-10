package com.simpleweather.simple_weather_api.controller;

import com.simpleweather.simple_weather_api.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class WeatherController {

    @Autowired private WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<?> getWeather(
            @RequestParam String pincode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate for_date) {
        return ResponseEntity.ok(weatherService.getWeather(pincode, for_date));
    }
}

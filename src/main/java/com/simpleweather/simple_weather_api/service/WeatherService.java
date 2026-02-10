package com.simpleweather.simple_weather_api.service;
import com.simpleweather.simple_weather_api.model.Location;
import com.simpleweather.simple_weather_api.model.WeatherData;
import com.simpleweather.simple_weather_api.repository.LocationRepository;
import com.simpleweather.simple_weather_api.repository.WeatherDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherService {

    private final LocationRepository locationRepo;
    private final WeatherDataRepository weatherRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(LocationRepository locationRepo, WeatherDataRepository weatherRepo) {
        this.locationRepo = locationRepo;
        this.weatherRepo = weatherRepo;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getWeather(String pincode, LocalDate forDate) {
        // 1. Check Cache (DB)
        var cachedData = weatherRepo.findByPincodeAndForDate(pincode, forDate);
        if (cachedData.isPresent()) return cachedData.get().getWeatherJson();

        // 2. Resolve Location
        var location = resolveLocation(pincode);

        // 3. Fetch from API
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude()
                + "&lon=" + location.getLongitude() + "&appid=" + apiKey;

        try {
            var response = restTemplate.getForObject(url, String.class);

            // 4. Save to Cache
            var newData = new WeatherData();
            newData.setPincode(pincode);
            newData.setForDate(forDate);
            newData.setWeatherJson(response);
            weatherRepo.save(newData);

            return response;
        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch weather data\"}";
        }
    }

    private Location resolveLocation(String pincode) {
        return locationRepo.findByPincode(pincode)
                .orElseGet(() -> fetchAndSaveLocation(pincode));
    }

    private Location fetchAndSaveLocation(String pincode) {
        String url = "http://api.openweathermap.org/geo/1.0/zip?zip=" + pincode + ",IN&appid=" + apiKey;
        try {
            var response = restTemplate.getForObject(url, String.class);
            var root = objectMapper.readTree(response);
            var newLoc = new Location(pincode, root.path("lat").asDouble(), root.path("lon").asDouble());
            return locationRepo.save(newLoc);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Pincode");
        }
    }
}
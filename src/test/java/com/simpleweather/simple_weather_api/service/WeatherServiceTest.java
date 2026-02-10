package com.simpleweather.simple_weather_api.service;

import com.simpleweather.simple_weather_api.model.Location;
import com.simpleweather.simple_weather_api.model.WeatherData;
import com.simpleweather.simple_weather_api.repository.LocationRepository;
import com.simpleweather.simple_weather_api.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private LocationRepository locationRepo;

    @Mock
    private WeatherDataRepository weatherRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    private final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        // Inject the API key value since @Value doesn't work in pure Mockito tests
        ReflectionTestUtils.setField(weatherService, "apiKey", API_KEY);
    }

    @Test
    void getWeather_ShouldReturnCachedData_WhenPresentInDB() {
        // GIVEN
        String pincode = "411014";
        LocalDate date = LocalDate.of(2020, 10, 15);
        String cachedJson = "{\"temp\": 25.0, \"source\": \"cache\"}";

        WeatherData mockData = new WeatherData();
        mockData.setPincode(pincode);
        mockData.setForDate(date);
        mockData.setWeatherJson(cachedJson);

        // Mock the DB returning data
        when(weatherRepo.findByPincodeAndForDate(pincode, date))
                .thenReturn(Optional.of(mockData));

        // WHEN
        String result = weatherService.getWeather(pincode, date);

        // THEN
        assertEquals(cachedJson, result);

        // CRITICAL: Verify that the External API was NEVER called
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getWeather_ShouldFetchFromApiAndSave_WhenNotInDB() {
        // GIVEN
        String pincode = "411014";
        LocalDate date = LocalDate.of(2020, 10, 15);
        String apiResponse = "{\"temp\": 30.0}";

        // 1. Mock DB returning empty (Cache Miss)
        when(weatherRepo.findByPincodeAndForDate(pincode, date)).thenReturn(Optional.empty());

        // 2. Mock Location Resolution (Assume location already exists in DB for simplicity)
        Location mockLocation = new Location(pincode, 18.52, 73.85);
        when(locationRepo.findByPincode(pincode)).thenReturn(Optional.of(mockLocation));

        // 3. Mock the External Weather API call
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // WHEN
        String result = weatherService.getWeather(pincode, date);

        // THEN
        assertEquals(apiResponse, result);

        // Verify that we tried to SAVE the new data to DB
        verify(weatherRepo, times(1)).save(any(WeatherData.class));
    }
}
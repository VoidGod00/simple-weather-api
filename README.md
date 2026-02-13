# Simple Weather API

A robust RESTful API built with **Java 21** and **Spring Boot** that provides weather information for a specific PIN code and date. It features an optimized caching mechanism using **MySQL** to minimize external API calls, adhering to strict TDD (Test Driven Development) principles.

***NOTE**=I am using render to deploy this project and it have a limitation that is "Your free instance will spin down with inactivity, which can delay requests by 50 seconds or more" due to which it is taking so much time to test in postman.

## 🚀 Live Demo
**Base URL:** `https://simple-weather-api-rh0l.onrender.com`

**Sample Request:**
`GET https://simple-weather-api-rh0l.onrender.com/api/weather?pincode=411014&for_date=2020-10-15`

---

## 🛠️ Tech Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.2.x
* **Database:** MySQL (Hosted on Aiven)
* **Build Tool:** Maven
* **Testing:** JUnit 5, Mockito (TDD)
* **Deployment:** Render (Dockerized)
* **External API:** OpenWeatherMap

---

## ✨ Key Features
1.  **REST API Endpoint:** A single endpoint to fetch weather data.
2.  **Smart Caching (Optimization):**
    * Checks the database first for existing weather data.
    * If data is missing, it fetches Lat/Long from the Geocoding API.
    * Fetches Weather Data from OpenWeatherMap.
    * Saves the result to the database to prevent future API calls for the same query.
3.  **Data Normalization:** Stores `Location` (Lat/Long) and `WeatherData` in separate tables.
4.  **Error Handling:** Graceful handling of invalid PIN codes or API failures.
5.  **Test Driven Development:** Core logic is verified using JUnit 5 and Mockito.

---

## ⚙️ Setup & Installation

### Prerequisites
* Java 21 SDK
* Maven
* MySQL Server (Local or Cloud)
* OpenWeatherMap API Key

### 1. Clone the Repository
```bash
git clone [https://github.com/VoidGod00/simple-weather-api.git](https://github.com/VoidGod00/simple-weather-api.git)
cd simple-weather-api

```

### 2. Configure Database & API Key

Update `src/main/resources/application.properties` with your credentials:

```properties
# Server Port
server.port=8080

# Database Configuration (Update with your Local or Cloud MySQL details)
spring.datasource.url=jdbc:mysql://localhost:3306/weather_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password

# OpenWeatherMap API Key
weather.api.key=YOUR_OPENWEATHER_API_KEY

```

Alternatively, you can set these as **Environment Variables** (Recommended for Production):

* `DB_URL`
* `DB_USER`
* `DB_PASSWORD`
* `WEATHER_API_KEY`

### 3. Build the Project

```bash
mvn clean package

```

### 4. Run the Application

```bash
java -jar target/simple-weather-api-0.0.1-SNAPSHOT.jar

```

Or using Maven:

```bash
mvn spring-boot:run

```

The API will start at `http://localhost:8080`.

---

## 🧪 Testing

Run the JUnit tests to verify the logic and caching behavior:

```bash
mvn test

```

---

## 📡 API Documentation

### Get Weather Info

**Endpoint:** `GET /api/weather`

**Parameters:**
| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `pincode` | String | Yes | The 6-digit PIN code of the location. |
| `for_date` | Date | Yes | The date in `YYYY-MM-DD` format. |

**Success Response (200 OK):**

```json
{
    "coord": {
        "lon": 73.85,
        "lat": 18.52
    },
    "weather": [
        {
            "id": 800,
            "main": "Clear",
            "description": "clear sky"
        }
    ],
    "main": {
        "temp": 298.15,
        "humidity": 45
    },
    "name": "Pune"
}

```

---

## 📂 Project Structure

```
src
├── main
│   ├── java/com/simpleweather/simple_weather_api
│   │   ├── controller      
│   │   │    └── WeatherController.java          # REST Controller
│   │   ├── model           
│   │   │    ├── Location.java                   # Location Entities
│   │   │    └── Location.java                   # WeatherData Entities
│   │   ├── repository                           # Data Access Layer
│   │   │    ├── LocationRepository.java
│   │   │    └── WeatherDataRepository.java 
│   │   ├── service         # Business Logic & Caching
│   │   │    └── WeatherService.java 
│   │   └── SimpleWeatherApiApplication.java
│   └── resources
│       └── application.properties
└── test/com.simpleweather.simple_weather_api.service
    └── WeatherServiceTest                   # JUnit Tests

```

---

## 🐳 Docker Deployment

To build and run using Docker:

```bash
docker build -t weather-api .
docker run -p 8080:8080 weather-api

```

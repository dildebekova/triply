package com.example.triply.data.network

import kotlinx.coroutines.delay

data class WeatherData(
    val temperature: Int,
    val condition: String,
    val icon: String
)

class WeatherService {
    // Mock weather service - in a real app, this would call an API like OpenWeatherMap
    suspend fun getWeather(location: String): WeatherData {
        delay(1000) // Simulate network delay
        val temp = (15..30).random()
        val conditions = listOf("Sunny", "Cloudy", "Partly Cloudy", "Rainy")
        return WeatherData(temp, conditions.random(), "")
    }
}

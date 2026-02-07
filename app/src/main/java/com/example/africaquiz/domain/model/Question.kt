package com.example.africaquiz.domain.model

data class Question(
    val countryName: String,
    val correctAnswer: String,
    val choices: List<String>,
    val capitalLat: Double,
    val capitalLng: Double
)

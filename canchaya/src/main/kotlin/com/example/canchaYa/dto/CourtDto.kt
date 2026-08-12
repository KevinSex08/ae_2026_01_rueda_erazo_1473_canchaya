package com.example.canchaYa.dto

data class CourtDto(
    val id: Long,
    val name: String,
    val isIndoor: Boolean,
    val pricePerHour: java.math.BigDecimal
)

data class CourtRequest(
    val name: String,
    val isIndoor: Boolean,
    val pricePerHour: java.math.BigDecimal
)

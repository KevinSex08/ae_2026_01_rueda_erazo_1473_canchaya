package com.example.canchaYa.dto

data class CourtDto(
    val id: Long,
    val name: String,
    val isIndoor: Boolean,
    val pricePerHour: java.math.BigDecimal,
    val location: String? = null,
    val type: String? = null,
    val surface: String? = null
)

data class CourtRequest(
    val name: String,
    val isIndoor: Boolean = false,
    val pricePerHour: java.math.BigDecimal,
    val location: String? = null,
    val type: String? = null,
    val surface: String? = null
)

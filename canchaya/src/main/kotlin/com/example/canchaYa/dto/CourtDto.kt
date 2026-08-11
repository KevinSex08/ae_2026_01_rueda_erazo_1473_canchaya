package com.example.canchaYa.dto

data class CourtDto(
    val id: Long,
    val name: String,
    val isIndoor: Boolean
)

data class CourtRequest(
    val name: String,
    val isIndoor: Boolean
)

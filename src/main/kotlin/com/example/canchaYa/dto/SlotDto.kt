package com.example.canchaya.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class SlotDto(
    val id: Long,
    val courtId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val price: BigDecimal
)

data class SlotRequest(
    val courtId: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val price: BigDecimal
)

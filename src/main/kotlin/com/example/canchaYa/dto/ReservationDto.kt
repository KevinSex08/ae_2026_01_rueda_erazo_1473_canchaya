package com.example.canchaya.dto

import com.example.canchaya.entity.enums.GameType
import com.example.canchaya.entity.enums.ReservationStatus
import java.time.LocalDateTime

data class ReservationDto(
    val id: Long,
    val slotId: Long,
    val cognitoUserId: String,
    val gameType: GameType,
    val status: ReservationStatus,
    val createdAt: LocalDateTime
)

data class ReservationRequest(
    val slotId: Long,
    val gameType: GameType
)

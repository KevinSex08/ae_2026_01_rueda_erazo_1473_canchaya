package com.example.canchaYa.dto

import com.example.canchaYa.entity.enums.GameType
import com.example.canchaYa.entity.enums.ReservationStatus
import java.time.LocalDateTime

data class ReservationDto(
    val id: Long,
    val slotIds: List<Long>,
    val cognitoUserId: String,
    val gameType: GameType,
    val status: ReservationStatus,
    val createdAt: LocalDateTime
)

data class ReservationRequest(
    val slotIds: List<Long>,
    val gameType: GameType
)

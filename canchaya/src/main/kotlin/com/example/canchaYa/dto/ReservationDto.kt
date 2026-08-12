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
    val createdAt: LocalDateTime,
    val slot: SlotDto? = null,
    val court: CourtDto? = null
)

data class ReservationRequest(
    val slotIds: List<Long> = emptyList(),
    val gameType: GameType = GameType.TRADITIONAL
)

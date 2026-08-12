package com.example.canchaYa.mapper

import com.example.canchaYa.dto.*
import com.example.canchaYa.entity.*

fun Court.toDto() = CourtDto(
    id = this.id,
    name = this.name,
    isIndoor = this.isIndoor,
    pricePerHour = this.pricePerHour,
    location = this.location,
    type = this.type,
    surface = this.surface
)

fun Slot.toDto(available: Boolean = true) = SlotDto(
    id = id,
    courtId = court.id,
    startTime = startTime,
    endTime = endTime,
    price = price,
    available = available
)

fun Reservation.toDto() = ReservationDto(
    id = id,
    slotIds = if (slot2 != null) listOf(slot.id, slot2!!.id) else listOf(slot.id),
    cognitoUserId = cognitoUserId,
    gameType = gameType,
    status = status,
    createdAt = createdAt,
    slot = slot.toDto(false),
    court = slot.court.toDto()
)

fun GameRecord.toDto() = GameRecordDto(
    id = id,
    reservationId = reservation.id,
    actualStartTime = actualStartTime,
    actualEndTime = actualEndTime,
    teamAScore = teamAScore,
    teamBScore = teamBScore,
    winnerTeam = winnerTeam,
    additionalStats = additionalStats
)

fun Player.toDto() = PlayerDto(
    id = id,
    gameRecordId = gameRecord.id,
    playerName = playerName,
    team = team
)

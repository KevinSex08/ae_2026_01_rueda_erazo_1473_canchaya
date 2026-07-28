package com.example.canchaya.mapper

import com.example.canchaya.dto.*
import com.example.canchaya.entity.*

fun Court.toDto() = CourtDto(
    id = id,
    name = name,
    isIndoor = isIndoor
)

fun Slot.toDto() = SlotDto(
    id = id,
    courtId = court.id,
    startTime = startTime,
    endTime = endTime,
    price = price
)

fun Reservation.toDto() = ReservationDto(
    id = id,
    slotId = slot.id,
    cognitoUserId = cognitoUserId,
    gameType = gameType,
    status = status,
    createdAt = createdAt
)

fun GameRecord.toDto() = GameRecordDto(
    id = id,
    reservationId = reservation.id,
    actualStartTime = actualStartTime,
    actualEndTime = actualEndTime,
    teamAScore = teamAScore,
    teamBScore = teamBScore,
    winnerTeam = winnerTeam
)

fun Player.toDto() = PlayerDto(
    id = id,
    gameRecordId = gameRecord.id,
    playerName = playerName,
    team = team
)

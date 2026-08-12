package com.example.canchaYa.dto

import com.example.canchaYa.entity.enums.Team
import java.time.LocalDateTime

data class GameRecordDto(
    val id: Long,
    val reservationId: Long,
    val actualStartTime: LocalDateTime?,
    val actualEndTime: LocalDateTime?,
    val teamAScore: Int,
    val teamBScore: Int,
    val winnerTeam: Team,
    val additionalStats: String?
)

data class GameRecordRequest(
    val reservationId: Long
)

data class UpdateScoreRequest(
    val teamAScore: Int,
    val teamBScore: Int,
    val winnerTeam: Team,
    val additionalStats: String?
)

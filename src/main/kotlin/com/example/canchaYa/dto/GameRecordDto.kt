package com.example.canchaya.dto

import com.example.canchaya.entity.enums.Team
import java.time.LocalDateTime

data class GameRecordDto(
    val id: Long,
    val reservationId: Long,
    val actualStartTime: LocalDateTime?,
    val actualEndTime: LocalDateTime?,
    val teamAScore: Int,
    val teamBScore: Int,
    val winnerTeam: Team
)

data class GameRecordRequest(
    val reservationId: Long
)

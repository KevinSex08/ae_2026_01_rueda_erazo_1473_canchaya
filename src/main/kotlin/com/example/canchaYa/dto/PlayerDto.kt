package com.example.canchaya.dto

import com.example.canchaya.entity.enums.Team

data class PlayerDto(
    val id: Long,
    val gameRecordId: Long,
    val playerName: String,
    val team: Team
)

data class PlayerRequest(
    val playerName: String,
    val team: Team
)

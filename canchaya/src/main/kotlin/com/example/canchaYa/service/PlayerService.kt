package com.example.canchaYa.service

import com.example.canchaYa.dto.PlayerDto
import com.example.canchaYa.dto.PlayerRequest
import com.example.canchaYa.entity.enums.GameType
import com.example.canchaYa.entity.enums.Team
import com.example.canchaYa.exception.ForbiddenException
import com.example.canchaYa.exception.ResourceNotFoundException
import com.example.canchaYa.mapper.toDto
import com.example.canchaYa.repository.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PlayerService(private val playerRepository: PlayerRepository) {

    fun updatePlayer(id: Long, request: PlayerRequest, cognitoUserId: String): PlayerDto {
        val player = playerRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Player with id $id not found")
        }
        // Assuming Player Dueño validation is tied to the Reservation's owner
        val reservationOwnerId = player.gameRecord.reservation.cognitoUserId
        if (reservationOwnerId != cognitoUserId) {
            throw ForbiddenException("You don't have permission to modify this player")
        }

        if (request.team == Team.NONE) {
            throw IllegalArgumentException("Player must be assigned to TEAM_A or TEAM_B")
        }

        val gameRecord = player.gameRecord
        val gameType = gameRecord.reservation.gameType

        // If team is changing, check limit
        if (player.team != request.team) {
            val players = playerRepository.findAll().filter { it.gameRecord.id == gameRecord.id && it.id != id }
            val teamCount = players.count { it.team == request.team }
            val maxPlayersPerTeam = if (gameType == GameType.SUPER_8) 4 else 2
            if (teamCount >= maxPlayersPerTeam) {
                throw IllegalArgumentException("Team ${request.team} already has the maximum of $maxPlayersPerTeam players for game type $gameType")
            }
        }

        player.playerName = request.playerName
        player.team = request.team
        return playerRepository.save(player).toDto()
    }

    fun deletePlayer(id: Long, cognitoUserId: String) {
        val player = playerRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Player with id $id not found")
        }
        val reservationOwnerId = player.gameRecord.reservation.cognitoUserId
        if (reservationOwnerId != cognitoUserId) {
            throw ForbiddenException("You don't have permission to delete this player")
        }
        playerRepository.delete(player)
    }
}

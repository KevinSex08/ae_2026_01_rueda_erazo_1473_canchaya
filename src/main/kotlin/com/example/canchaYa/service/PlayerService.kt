package com.example.canchaya.service

import com.example.canchaya.dto.PlayerDto
import com.example.canchaya.dto.PlayerRequest
import com.example.canchaya.exception.ForbiddenException
import com.example.canchaya.exception.ResourceNotFoundException
import com.example.canchaya.mapper.toDto
import com.example.canchaya.repository.PlayerRepository
import org.springframework.stereotype.Service

@Service
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

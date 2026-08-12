package com.example.canchaYa.service

import com.example.canchaYa.dto.GameRecordDto
import com.example.canchaYa.dto.GameRecordRequest
import com.example.canchaYa.dto.PlayerDto
import com.example.canchaYa.dto.PlayerRequest
import com.example.canchaYa.entity.GameRecord
import com.example.canchaYa.entity.Player
import com.example.canchaYa.entity.enums.GameType
import com.example.canchaYa.entity.enums.Team
import com.example.canchaYa.exception.ResourceNotFoundException
import com.example.canchaYa.mapper.toDto
import com.example.canchaYa.repository.GameRecordRepository
import com.example.canchaYa.repository.PlayerRepository
import com.example.canchaYa.repository.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class GameRecordService(
    private val gameRecordRepository: GameRecordRepository,
    private val reservationRepository: ReservationRepository,
    private val playerRepository: PlayerRepository
) {

    fun createGameRecord(request: GameRecordRequest): GameRecordDto {
        val existing = gameRecordRepository.findByReservationId(request.reservationId)
        if (existing != null) {
            return existing.toDto()
        }

        val reservation = reservationRepository.findById(request.reservationId).orElseThrow {
            ResourceNotFoundException("Reservation with id ${request.reservationId} not found")
        }
        val gameRecord = GameRecord(
            reservation = reservation
        )
        return gameRecordRepository.save(gameRecord).toDto()
    }

    fun getMyGameRecords(cognitoUserId: String): List<GameRecordDto> {
        return gameRecordRepository.findByReservation_CognitoUserId(cognitoUserId).map { it.toDto() }
    }

    fun getGameRecordById(id: Long): GameRecordDto {
        val gameRecord = gameRecordRepository.findById(id).orElseThrow {
            ResourceNotFoundException("GameRecord with id $id not found")
        }
        return gameRecord.toDto()
    }

    fun startGame(id: Long): GameRecordDto {
        val gameRecord = gameRecordRepository.findById(id).orElseThrow {
            ResourceNotFoundException("GameRecord with id $id not found")
        }
        gameRecord.actualStartTime = LocalDateTime.now()
        return gameRecordRepository.save(gameRecord).toDto()
    }

    fun finishGame(id: Long): GameRecordDto {
        val gameRecord = gameRecordRepository.findById(id).orElseThrow {
            ResourceNotFoundException("GameRecord with id $id not found")
        }
        gameRecord.actualEndTime = LocalDateTime.now()
        // Here we could calculate the winner based on scores, but assuming scores are updated elsewhere or later
        return gameRecordRepository.save(gameRecord).toDto()
    }

    fun updateScore(id: Long, request: com.example.canchaYa.dto.UpdateScoreRequest): GameRecordDto {
        val gameRecord = gameRecordRepository.findById(id).orElseThrow {
            ResourceNotFoundException("GameRecord with id $id not found")
        }
        gameRecord.teamAScore = request.teamAScore
        gameRecord.teamBScore = request.teamBScore
        gameRecord.winnerTeam = request.winnerTeam
        gameRecord.additionalStats = request.additionalStats
        return gameRecordRepository.save(gameRecord).toDto()
    }

    fun deleteGameRecord(id: Long) {
        val gameRecord = gameRecordRepository.findById(id).orElseThrow {
            ResourceNotFoundException("GameRecord with id $id not found")
        }
        gameRecordRepository.delete(gameRecord)
    }

    fun addPlayer(gameRecordId: Long, request: PlayerRequest): PlayerDto {
        val gameRecord = gameRecordRepository.findById(gameRecordId).orElseThrow {
            ResourceNotFoundException("GameRecord with id $gameRecordId not found")
        }
        
        if (request.team == Team.NONE) {
            throw IllegalArgumentException("Player must be assigned to TEAM_A or TEAM_B")
        }

        val gameType = gameRecord.reservation.gameType
        val currentPlayers = playerRepository.findAll().filter { it.gameRecord.id == gameRecordId }

        val maxPlayersPerTeam = if (gameType == GameType.SUPER_8) 4 else 2
        val maxTotalPlayers = if (gameType == GameType.SUPER_8) 8 else 4

        if (currentPlayers.size >= maxTotalPlayers) {
            throw IllegalArgumentException("Game record already has the maximum of $maxTotalPlayers players for game type $gameType")
        }
        if (currentPlayers.count { it.team == request.team } >= maxPlayersPerTeam) {
            throw IllegalArgumentException("Team ${request.team} already has the maximum of $maxPlayersPerTeam players for game type $gameType")
        }

        val player = Player(
            gameRecord = gameRecord,
            playerName = request.playerName,
            team = request.team
        )
        val saved = playerRepository.save(player)
        return saved.toDto()
    }

    fun getPlayersByGameRecordId(gameRecordId: Long): List<PlayerDto> {
        val gameRecord = gameRecordRepository.findById(gameRecordId).orElseThrow {
            ResourceNotFoundException("GameRecord with id $gameRecordId not found")
        }
        // Ideally playerRepository.findByGameRecordId
        // But for simplicity we can fetch all and filter or add the method. Let's add the method.
        return playerRepository.findAll().filter { it.gameRecord.id == gameRecordId }.map { it.toDto() }
    }
}

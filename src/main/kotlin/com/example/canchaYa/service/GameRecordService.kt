package com.example.canchaya.service

import com.example.canchaya.dto.GameRecordDto
import com.example.canchaya.dto.GameRecordRequest
import com.example.canchaya.dto.PlayerDto
import com.example.canchaya.dto.PlayerRequest
import com.example.canchaya.entity.GameRecord
import com.example.canchaya.entity.Player
import com.example.canchaya.exception.ResourceNotFoundException
import com.example.canchaya.mapper.toDto
import com.example.canchaya.repository.GameRecordRepository
import com.example.canchaya.repository.PlayerRepository
import com.example.canchaya.repository.ReservationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GameRecordService(
    private val gameRecordRepository: GameRecordRepository,
    private val reservationRepository: ReservationRepository,
    private val playerRepository: PlayerRepository
) {

    fun createGameRecord(request: GameRecordRequest): GameRecordDto {
        val reservation = reservationRepository.findById(request.reservationId).orElseThrow {
            ResourceNotFoundException("Reservation with id ${request.reservationId} not found")
        }
        val gameRecord = GameRecord(
            reservation = reservation
        )
        return gameRecordRepository.save(gameRecord).toDto()
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
        val player = Player(
            gameRecord = gameRecord,
            playerName = request.playerName,
            team = request.team
        )
        return playerRepository.save(player).toDto()
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

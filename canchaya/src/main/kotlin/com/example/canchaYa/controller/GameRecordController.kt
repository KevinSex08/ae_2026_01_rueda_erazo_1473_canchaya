package com.example.canchaYa.controller

import com.example.canchaYa.dto.GameRecordDto
import com.example.canchaYa.dto.GameRecordRequest
import com.example.canchaYa.dto.PlayerDto
import com.example.canchaYa.dto.PlayerRequest
import com.example.canchaYa.service.GameRecordService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/game-records")
class GameRecordController(private val gameRecordService: GameRecordService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGameRecord(@RequestBody request: GameRecordRequest): GameRecordDto =
        gameRecordService.createGameRecord(request)

    @GetMapping("/my")
    fun getMyGameRecords(@org.springframework.security.core.annotation.AuthenticationPrincipal jwt: org.springframework.security.oauth2.jwt.Jwt): List<GameRecordDto> {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        return gameRecordService.getMyGameRecords(userId)
    }

    @GetMapping("/{id}")
    fun getGameRecordById(@PathVariable id: Long): GameRecordDto =
        gameRecordService.getGameRecordById(id)

    @PatchMapping("/{id}/start")
    fun startGame(@PathVariable id: Long): GameRecordDto =
        gameRecordService.startGame(id)

    @PatchMapping("/{id}/finish")
    fun finishGame(@PathVariable id: Long): GameRecordDto =
        gameRecordService.finishGame(id)

    @PutMapping("/{id}/score")
    fun updateScore(@PathVariable id: Long, @RequestBody request: com.example.canchaYa.dto.UpdateScoreRequest): GameRecordDto =
        gameRecordService.updateScore(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteGameRecord(@PathVariable id: Long) {
        gameRecordService.deleteGameRecord(id)
    }

    // Players endpoints inside GameRecords as per matrix:
    // POST /api/v1/game-records/{id}/players
    // GET /api/v1/game-records/{id}/players

    @PostMapping("/{id}/players")
    @ResponseStatus(HttpStatus.CREATED)
    fun addPlayer(@PathVariable id: Long, @RequestBody request: PlayerRequest): PlayerDto =
        gameRecordService.addPlayer(id, request)

    @GetMapping("/{id}/players")
    fun getPlayersByGameRecordId(@PathVariable id: Long): List<PlayerDto> =
        gameRecordService.getPlayersByGameRecordId(id)
}

package com.example.canchaya.controller

import com.example.canchaya.dto.GameRecordDto
import com.example.canchaya.dto.GameRecordRequest
import com.example.canchaya.dto.PlayerDto
import com.example.canchaya.dto.PlayerRequest
import com.example.canchaya.service.GameRecordService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/game-records")
class GameRecordController(private val gameRecordService: GameRecordService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGameRecord(@RequestBody request: GameRecordRequest): GameRecordDto =
        gameRecordService.createGameRecord(request)

    @GetMapping("/{id}")
    fun getGameRecordById(@PathVariable id: Long): GameRecordDto =
        gameRecordService.getGameRecordById(id)

    @PatchMapping("/{id}/start")
    fun startGame(@PathVariable id: Long): GameRecordDto =
        gameRecordService.startGame(id)

    @PatchMapping("/{id}/finish")
    fun finishGame(@PathVariable id: Long): GameRecordDto =
        gameRecordService.finishGame(id)

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

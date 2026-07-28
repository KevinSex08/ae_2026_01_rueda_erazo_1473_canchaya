package com.example.canchaya.controller

import com.example.canchaya.dto.PlayerDto
import com.example.canchaya.dto.PlayerRequest
import com.example.canchaya.service.PlayerService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/players")
class PlayerController(private val playerService: PlayerService) {

    @PutMapping("/{id}")
    fun updatePlayer(
        @PathVariable id: Long,
        @RequestBody request: PlayerRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): PlayerDto {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        return playerService.updatePlayer(id, request, userId)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePlayer(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwt: Jwt
    ) {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        playerService.deletePlayer(id, userId)
    }
}

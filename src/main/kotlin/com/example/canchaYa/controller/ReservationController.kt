package com.example.canchaya.controller

import com.example.canchaya.dto.ReservationDto
import com.example.canchaya.dto.ReservationRequest
import com.example.canchaya.service.ReservationService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/reservations")
class ReservationController(private val reservationService: ReservationService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(
        @RequestBody request: ReservationRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ReservationDto {
        // We use the subject (sub) or username from JWT as the cognitoUserId
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        return reservationService.createReservation(request, userId)
    }

    @GetMapping("/my")
    fun getMyReservations(@AuthenticationPrincipal jwt: Jwt): List<ReservationDto> {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        return reservationService.getMyReservations(userId)
    }

    @GetMapping("/{id}")
    fun getReservationById(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwt: Jwt
    ): ReservationDto {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        val isAdmin = jwt.claims["cognito:groups"]?.let {
            @Suppress("UNCHECKED_CAST")
            (it as List<String>).contains("ADMIN")
        } ?: false
        
        return reservationService.getReservationById(id, userId, isAdmin)
    }

    @PatchMapping("/{id}/cancel")
    fun cancelReservation(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwt: Jwt
    ): ReservationDto {
        val userId = jwt.subject ?: jwt.getClaimAsString("username") ?: "unknown"
        return reservationService.cancelReservation(id, userId)
    }

    @PatchMapping("/{id}/confirm")
    fun confirmReservation(@PathVariable id: Long): ReservationDto {
        return reservationService.confirmReservation(id)
    }
}

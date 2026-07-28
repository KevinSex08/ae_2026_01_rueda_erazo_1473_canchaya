package com.example.canchaya.service

import com.example.canchaya.dto.ReservationDto
import com.example.canchaya.dto.ReservationRequest
import com.example.canchaya.entity.Reservation
import com.example.canchaya.entity.enums.ReservationStatus
import com.example.canchaya.exception.ForbiddenException
import com.example.canchaya.exception.ResourceNotFoundException
import com.example.canchaya.mapper.toDto
import com.example.canchaya.repository.ReservationRepository
import com.example.canchaya.repository.SlotRepository
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val slotRepository: SlotRepository
) {

    fun createReservation(request: ReservationRequest, cognitoUserId: String): ReservationDto {
        val slot = slotRepository.findById(request.slotId).orElseThrow {
            ResourceNotFoundException("Slot with id ${request.slotId} not found")
        }
        val reservation = Reservation(
            slot = slot,
            cognitoUserId = cognitoUserId,
            gameType = request.gameType,
            status = ReservationStatus.PENDING
        )
        return reservationRepository.save(reservation).toDto()
    }

    fun getMyReservations(cognitoUserId: String): List<ReservationDto> {
        return reservationRepository.findByCognitoUserId(cognitoUserId).map { it.toDto() }
    }

    fun getReservationById(id: Long, cognitoUserId: String, isAdmin: Boolean): ReservationDto {
        val reservation = reservationRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Reservation with id $id not found")
        }
        if (!isAdmin && reservation.cognitoUserId != cognitoUserId) {
            throw ForbiddenException("You don't have permission to view this reservation")
        }
        return reservation.toDto()
    }

    fun cancelReservation(id: Long, cognitoUserId: String): ReservationDto {
        val reservation = reservationRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Reservation with id $id not found")
        }
        if (reservation.cognitoUserId != cognitoUserId) {
            throw ForbiddenException("You don't have permission to cancel this reservation")
        }
        reservation.status = ReservationStatus.CANCELLED
        return reservationRepository.save(reservation).toDto()
    }

    fun confirmReservation(id: Long): ReservationDto {
        val reservation = reservationRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Reservation with id $id not found")
        }
        reservation.status = ReservationStatus.CONFIRMED
        return reservationRepository.save(reservation).toDto()
    }
}

package com.example.canchaYa.service

import com.example.canchaYa.dto.ReservationDto
import com.example.canchaYa.dto.ReservationRequest
import com.example.canchaYa.entity.Reservation
import com.example.canchaYa.entity.enums.GameType
import com.example.canchaYa.entity.enums.ReservationStatus
import com.example.canchaYa.exception.ConflictException
import com.example.canchaYa.exception.ForbiddenException
import com.example.canchaYa.exception.ResourceNotFoundException
import com.example.canchaYa.mapper.toDto
import com.example.canchaYa.repository.ReservationRepository
import com.example.canchaYa.repository.SlotRepository
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val slotRepository: SlotRepository
) {

    fun createReservation(request: ReservationRequest, cognitoUserId: String): ReservationDto {
        // Validate gameType and slotIds
        if (request.gameType == GameType.SUPER_8 && request.slotIds.size != 2) {
            throw IllegalArgumentException("SUPER_8 game type requires exactly 2 slot IDs")
        }
        if (request.gameType == GameType.TRADITIONAL && request.slotIds.size != 1) {
            throw IllegalArgumentException("TRADITIONAL game type requires exactly 1 slot ID")
        }

        // Validate duplicates and availability
        val allReservations = reservationRepository.findAll()
        for (slotId in request.slotIds) {
            // Check if slot is already confirmed
            val isConfirmed = allReservations.any {
                it.status == ReservationStatus.CONFIRMED && (it.slot.id == slotId || it.slot2?.id == slotId)
            }
            if (isConfirmed) {
                throw ConflictException("Slot with id $slotId is already confirmed")
            }

            // Check if the user already requested this slot
            val userAlreadyRequested = allReservations.any {
                it.cognitoUserId == cognitoUserId &&
                it.status in listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED) &&
                (it.slot.id == slotId || it.slot2?.id == slotId)
            }
            if (userAlreadyRequested) {
                throw ConflictException("You have already requested slot $slotId")
            }
        }

        val slot1 = slotRepository.findById(request.slotIds[0]).orElseThrow {
            ResourceNotFoundException("Slot with id ${request.slotIds[0]} not found")
        }
        val slot2 = if (request.slotIds.size > 1) {
            slotRepository.findById(request.slotIds[1]).orElseThrow {
                ResourceNotFoundException("Slot with id ${request.slotIds[1]} not found")
            }
        } else null

        val reservation = Reservation(
            slot = slot1,
            slot2 = slot2,
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
        val reservationToConfirm = reservationRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Reservation with id $id not found")
        }
        reservationToConfirm.status = ReservationStatus.CONFIRMED
        reservationRepository.save(reservationToConfirm)

        val confirmedSlotIds = listOfNotNull(reservationToConfirm.slot.id, reservationToConfirm.slot2?.id)

        // Cancel other pending reservations that request the same slots
        val pendingReservations = reservationRepository.findAll().filter { it.status == ReservationStatus.PENDING }
        for (pending in pendingReservations) {
            if (pending.id != id) {
                val pendingSlotIds = listOfNotNull(pending.slot.id, pending.slot2?.id)
                if (pendingSlotIds.any { it in confirmedSlotIds }) {
                    pending.status = ReservationStatus.CANCELLED
                    reservationRepository.save(pending)
                }
            }
        }

        return reservationToConfirm.toDto()
    }
}

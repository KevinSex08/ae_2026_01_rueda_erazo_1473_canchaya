package com.example.canchaYa.service

import com.example.canchaYa.dto.SlotDto
import com.example.canchaYa.dto.SlotRequest
import com.example.canchaYa.entity.Slot
import com.example.canchaYa.exception.ResourceNotFoundException
import com.example.canchaYa.mapper.toDto
import com.example.canchaYa.repository.CourtRepository
import com.example.canchaYa.repository.SlotRepository
import com.example.canchaYa.repository.ReservationRepository
import com.example.canchaYa.entity.enums.ReservationStatus
import org.springframework.stereotype.Service

@Service
class SlotService(
    private val slotRepository: SlotRepository,
    private val courtRepository: CourtRepository,
    private val reservationRepository: ReservationRepository
) {

    fun getAllSlots(): List<SlotDto> {
        val confirmedReservations = reservationRepository.findAll().filter { it.status == ReservationStatus.CONFIRMED }
        val confirmedSlotIds = confirmedReservations.flatMap { 
            listOfNotNull(it.slot.id, it.slot2?.id)
        }.toSet()
        
        return slotRepository.findAll().map { it.toDto(available = !confirmedSlotIds.contains(it.id)) }
    }

    fun getAvailableSlots(courtId: Long?, date: String?): List<SlotDto> {
        val allSlots = if (courtId != null) {
            slotRepository.findByCourtId(courtId)
        } else {
            slotRepository.findAll()
        }
        
        // Regla 1 y 2: Filtrar tiempo pasado y horario operativo (10:00 AM a 22:00 PM)
        val ecuadorZone = java.time.ZoneId.of("America/Guayaquil")
        val now = java.time.LocalDateTime.now(ecuadorZone)
        val validSlots = allSlots.filter { 
            it.startTime.isAfter(now) && it.startTime.hour in 10..21
        }
        
        // Regla 3: Filtrar reservaciones CONFIRMADAS y PENDIENTES para evitar colisiones concurrentes tempranas
        val blockingReservations = reservationRepository.findAll().filter { 
            it.status == ReservationStatus.CONFIRMED || it.status == ReservationStatus.PENDING 
        }
        val blockedSlotIds = blockingReservations.flatMap { 
            listOfNotNull(it.slot.id, it.slot2?.id)
        }.toSet()
        
        return validSlots.map { it.toDto(available = !blockedSlotIds.contains(it.id)) }
    }

    fun getSlotById(id: Long): SlotDto {
        val slot = slotRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Slot with id $id not found")
        }
        return slot.toDto()
    }

    fun createSlot(request: SlotRequest): SlotDto {
        val court = courtRepository.findById(request.courtId).orElseThrow {
            ResourceNotFoundException("Court with id ${request.courtId} not found")
        }
        val slot = Slot(
            court = court,
            startTime = request.startTime,
            endTime = request.endTime,
            price = request.price
        )
        return slotRepository.save(slot).toDto()
    }

    fun updateSlot(id: Long, request: SlotRequest): SlotDto {
        val slot = slotRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Slot with id $id not found")
        }
        val court = courtRepository.findById(request.courtId).orElseThrow {
            ResourceNotFoundException("Court with id ${request.courtId} not found")
        }
        slot.court = court
        slot.startTime = request.startTime
        slot.endTime = request.endTime
        slot.price = request.price
        return slotRepository.save(slot).toDto()
    }

    fun deleteSlot(id: Long) {
        val slot = slotRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Slot with id $id not found")
        }
        slotRepository.delete(slot)
    }
}

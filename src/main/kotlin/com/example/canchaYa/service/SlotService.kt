package com.example.canchaya.service

import com.example.canchaya.dto.SlotDto
import com.example.canchaya.dto.SlotRequest
import com.example.canchaya.entity.Slot
import com.example.canchaya.exception.ResourceNotFoundException
import com.example.canchaya.mapper.toDto
import com.example.canchaya.repository.CourtRepository
import com.example.canchaya.repository.SlotRepository
import org.springframework.stereotype.Service

@Service
class SlotService(
    private val slotRepository: SlotRepository,
    private val courtRepository: CourtRepository
) {

    fun getAllSlots(): List<SlotDto> {
        return slotRepository.findAll().map { it.toDto() }
    }

    fun getAvailableSlots(): List<SlotDto> {
        // Here we could filter slots that don't have reservations, but for now we just return all
        // In a real scenario, we would check the Reservation table to exclude booked ones
        return slotRepository.findAll().map { it.toDto() }
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

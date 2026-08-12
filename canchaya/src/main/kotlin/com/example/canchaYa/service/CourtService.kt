package com.example.canchaYa.service

import com.example.canchaYa.dto.CourtDto
import com.example.canchaYa.dto.CourtRequest
import com.example.canchaYa.entity.Court
import com.example.canchaYa.exception.ResourceNotFoundException
import com.example.canchaYa.mapper.toDto
import com.example.canchaYa.repository.CourtRepository
import org.springframework.stereotype.Service

@Service
class CourtService(private val courtRepository: CourtRepository) {

    fun getAllCourts(): List<CourtDto> {
        return courtRepository.findAll().map { it.toDto() }
    }

    fun getCourtById(id: Long): CourtDto {
        val court = courtRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Court with id $id not found")
        }
        return court.toDto()
    }

    fun createCourt(request: CourtRequest): CourtDto {
        val court = Court(
            name = request.name,
            isIndoor = request.type?.equals("INDOOR", ignoreCase = true) ?: request.isIndoor,
            pricePerHour = request.pricePerHour,
            location = request.location,
            type = request.type,
            surface = request.surface
        )
        return courtRepository.save(court).toDto()
    }

    fun updateCourt(id: Long, request: CourtRequest): CourtDto {
        val court = courtRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Court with id $id not found")
        }
        court.name = request.name
        court.isIndoor = request.type?.equals("INDOOR", ignoreCase = true) ?: request.isIndoor
        court.pricePerHour = request.pricePerHour
        court.location = request.location
        court.type = request.type
        court.surface = request.surface
        return courtRepository.save(court).toDto()
    }

    fun deleteCourt(id: Long) {
        val court = courtRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Court with id $id not found")
        }
        courtRepository.delete(court)
    }
}

package com.example.canchaYa.controller

import com.example.canchaYa.dto.CourtDto
import com.example.canchaYa.dto.CourtRequest
import com.example.canchaYa.service.CourtService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/courts")
class CourtController(private val courtService: CourtService) {

    @GetMapping
    fun getAllCourts(): List<CourtDto> = courtService.getAllCourts()

    @GetMapping("/{id}")
    fun getCourtById(@PathVariable id: Long): CourtDto = courtService.getCourtById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourt(@RequestBody request: CourtRequest): CourtDto = courtService.createCourt(request)

    @PutMapping("/{id}")
    fun updateCourt(@PathVariable id: Long, @RequestBody request: CourtRequest): CourtDto =
        courtService.updateCourt(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourt(@PathVariable id: Long) {
        courtService.deleteCourt(id)
    }
}

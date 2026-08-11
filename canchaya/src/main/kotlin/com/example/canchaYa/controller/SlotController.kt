package com.example.canchaYa.controller

import com.example.canchaYa.dto.SlotDto
import com.example.canchaYa.dto.SlotRequest
import com.example.canchaYa.service.SlotService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/slots")
class SlotController(private val slotService: SlotService) {

    @GetMapping
    fun getAllSlots(): List<SlotDto> = slotService.getAllSlots()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSlot(@RequestBody request: SlotRequest): SlotDto = slotService.createSlot(request)

    @PutMapping("/{id}")
    fun updateSlot(@PathVariable id: Long, @RequestBody request: SlotRequest): SlotDto =
        slotService.updateSlot(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSlot(@PathVariable id: Long) {
        slotService.deleteSlot(id)
    }
}

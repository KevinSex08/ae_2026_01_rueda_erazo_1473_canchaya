package com.example.canchaYa.controller

import com.example.canchaYa.dto.SlotRequest
import com.example.canchaYa.service.SlotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

data class BulkSlotRequest(
    val courtId: Long,
    val date: String,
    val startTime: String,
    val endTime: String,
    val slotDuration: Int,
    val price: BigDecimal
)

@RestController
@RequestMapping("/api/v1/admin/slots")
class AdminSlotController(private val slotService: SlotService) {

    @PostMapping
    fun createBulkSlots(@RequestBody request: BulkSlotRequest): ResponseEntity<Map<String, String>> {
        val targetDate = LocalDate.parse(request.date)
        val startT = LocalTime.parse(request.startTime)
        val endT = LocalTime.parse(request.endTime)
        
        var currentT = startT
        var createdCount = 0

        while (currentT.isBefore(endT)) {
            val nextT = currentT.plusMinutes(request.slotDuration.toLong())
            if (nextT.isAfter(endT)) break // don't exceed end time

            val slotReq = SlotRequest(
                courtId = request.courtId,
                startTime = LocalDateTime.of(targetDate, currentT),
                endTime = LocalDateTime.of(targetDate, nextT),
                price = request.price
            )
            slotService.createSlot(slotReq)
            createdCount++
            currentT = nextT
        }

        return ResponseEntity.ok(mapOf("message" to "$createdCount slots generated successfully"))
    }
}

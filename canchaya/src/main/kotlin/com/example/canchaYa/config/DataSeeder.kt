package com.example.canchaYa.config

import com.example.canchaYa.entity.Court
import com.example.canchaYa.entity.Slot
import com.example.canchaYa.repository.CourtRepository
import com.example.canchaYa.repository.SlotRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Component
class DataSeeder(
    private val courtRepository: CourtRepository,
    private val slotRepository: SlotRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val courts = courtRepository.findAll()
        val savedCourts = if (courts.isEmpty()) {
            println("Seeding database with realistic courts for CanchaYA...")
            val courtsToCreate = listOf(
                Court(name = "CanchaYA - Cristal Indoor 1", isIndoor = true, pricePerHour = BigDecimal("40.00")),
                Court(name = "CanchaYA - Panorámica Indoor 2", isIndoor = true, pricePerHour = BigDecimal("45.00")),
                Court(name = "CanchaYA - Cristal Outdoor 3", isIndoor = false, pricePerHour = BigDecimal("25.00")),
                Court(name = "CanchaYA - Muro Clásico Outdoor 4", isIndoor = false, pricePerHour = BigDecimal("20.00"))
            )
            courtRepository.saveAll(courtsToCreate).also {
                println("Created ${it.size} courts for CanchaYA.")
            }
        } else {
            courts
        }

        val today = LocalDate.now()
        val hasFutureSlots = slotRepository.findAll().any { it.startTime.isAfter(today.atStartOfDay()) }
        if (hasFutureSlots) {
            println("Database already has future slots. Skipping slot seeding.")
            return
        }

        println("Seeding database with new future slots for CanchaYA...")
        val slotsToCreate = mutableListOf<Slot>()

        // Loop over the next 7 days (including today)
        for (dayOffset in 0..6) {
            val date = today.plusDays(dayOffset.toLong())
            
            for (court in savedCourts) {
                // Determine price dynamically:
                // Indoor and panoramic/glass/premium courts: $35.00 to $45.00
                // Outdoor wall/classic courts: $20.00 to $25.00
                val price = when {
                    court.isIndoor && (court.name.contains("Panorámica", true) || court.name.contains("Premium", true) || court.name.contains("Glass", true)) -> BigDecimal("45.00")
                    court.isIndoor -> BigDecimal("35.00")
                    court.name.contains("Muro", true) || court.name.contains("Clásico", true) -> BigDecimal("20.00")
                    else -> BigDecimal("25.00")
                }

                // Daily hours from 06:00 to 22:30 in blocks of 90 minutes (1.5 hours)
                // Start time ranges from 06:00 to 21:00 (since 21:00 + 90 mins = 22:30)
                var startTime = date.atTime(LocalTime.of(6, 0))
                val endOfDay = date.atTime(LocalTime.of(22, 30))

                while (startTime.plusMinutes(90).isBefore(endOfDay) || startTime.plusMinutes(90).isEqual(endOfDay)) {
                    val endTime = startTime.plusMinutes(90)
                    slotsToCreate.add(
                        Slot(
                            court = court,
                            startTime = startTime,
                            endTime = endTime,
                            price = price
                        )
                    )
                    startTime = endTime
                }
            }
        }

        // Save all slots
        val savedSlots = slotRepository.saveAll(slotsToCreate)
        println("Successfully seeded ${savedSlots.size} slots across 7 days for CanchaYA.")
    }
}

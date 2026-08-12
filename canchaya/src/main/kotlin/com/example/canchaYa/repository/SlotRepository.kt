package com.example.canchaYa.repository

import com.example.canchaYa.entity.Slot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SlotRepository : JpaRepository<Slot, Long> {
    fun findByCourtId(courtId: Long): List<Slot>
}

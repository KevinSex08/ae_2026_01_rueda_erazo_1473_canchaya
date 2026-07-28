package com.example.canchaya.repository

import com.example.canchaya.entity.Slot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SlotRepository : JpaRepository<Slot, Long>

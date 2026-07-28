package com.example.canchaya.repository

import com.example.canchaya.entity.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun findByCognitoUserId(cognitoUserId: String): List<Reservation>
}

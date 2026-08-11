package com.example.canchaYa.repository

import com.example.canchaYa.entity.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun findByCognitoUserId(cognitoUserId: String): List<Reservation>
}

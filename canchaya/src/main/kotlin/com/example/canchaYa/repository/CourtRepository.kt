package com.example.canchaYa.repository

import com.example.canchaYa.entity.Court
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourtRepository : JpaRepository<Court, Long>

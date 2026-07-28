package com.example.canchaya.repository

import com.example.canchaya.entity.Court
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourtRepository : JpaRepository<Court, Long>

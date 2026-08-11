package com.example.canchaYa.repository

import com.example.canchaYa.entity.GameRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRecordRepository : JpaRepository<GameRecord, Long>

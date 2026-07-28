package com.example.canchaya.repository

import com.example.canchaya.entity.GameRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRecordRepository : JpaRepository<GameRecord, Long>

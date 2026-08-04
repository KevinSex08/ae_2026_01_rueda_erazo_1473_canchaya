package com.example.canchaya.entity

import com.example.canchaYa.entity.BaseEntity
import com.example.canchaya.entity.enums.GameType
import com.example.canchaya.entity.enums.ReservationStatus
import jakarta.persistence.*

@Entity
@Table(name = "reservations")
class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    var slot: Slot,

    @Column(name = "cognito_user_id", nullable = false, length = 100)
    var cognitoUserId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    var gameType: GameType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot2_id")
    var slot2: Slot? = null
) : BaseEntity()

package com.example.canchaYa.entity

import com.example.canchaYa.entity.BaseEntity
import com.example.canchaYa.entity.enums.Team
import jakarta.persistence.*

@Entity
@Table(name = "players")
class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_record_id", nullable = false)
    var gameRecord: GameRecord,

    @Column(name = "player_name", nullable = false, length = 80)
    var playerName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var team: Team
) : BaseEntity()

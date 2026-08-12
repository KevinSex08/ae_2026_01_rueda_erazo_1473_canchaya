package com.example.canchaYa.entity

import com.example.canchaYa.entity.BaseEntity
import com.example.canchaYa.entity.enums.Team
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_records")
class GameRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    var reservation: Reservation,

    @Column(name = "actual_start_time")
    var actualStartTime: LocalDateTime? = null,

    @Column(name = "actual_end_time")
    var actualEndTime: LocalDateTime? = null,

    @Column(name = "team_a_score", nullable = false)
    var teamAScore: Int = 0,

    @Column(name = "team_b_score", nullable = false)
    var teamBScore: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "winner_team", nullable = false)
    var winnerTeam: Team = Team.NONE,

    @Column(name = "additional_stats", columnDefinition = "TEXT")
    var additionalStats: String? = null
) : BaseEntity()

package com.example.canchaya.entity

import jakarta.persistence.*

@Entity
@Table(name = "courts")
class Court(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(nullable = false, name = "is_indoor")
    var isIndoor: Boolean = false
)

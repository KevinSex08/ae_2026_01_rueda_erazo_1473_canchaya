package com.example.canchaYa.entity

import com.example.canchaYa.entity.BaseEntity
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
    var isIndoor: Boolean = false,

    @Column(nullable = false, name = "price_per_hour")
    var pricePerHour: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(nullable = true, length = 100)
    var location: String? = null,

    @Column(nullable = true, length = 50)
    var type: String? = null,

    @Column(nullable = true, length = 50)
    var surface: String? = null
) : BaseEntity()

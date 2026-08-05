package com.example.users.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "cognito_sub", nullable = false, unique = true, length = 100)
    var cognitoSub: String,

    @Column(nullable = false, length = 150)
    var email: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 50)
    var role: String
) : BaseEntity()

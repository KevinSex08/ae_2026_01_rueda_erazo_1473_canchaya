package com.example.users.dto

data class UserDto(
    val id: Long,
    val cognitoSub: String,
    val email: String,
    val name: String,
    val role: String
)

data class UserRequest(
    val email: String,
    val name: String,
    val role: String
)

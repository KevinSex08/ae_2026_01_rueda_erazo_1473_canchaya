package com.example.users.service

import com.example.users.dto.UserDto
import com.example.users.dto.UserRequest
import com.example.users.entity.User
import com.example.users.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun registerUser(cognitoSub: String, request: UserRequest): UserDto {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("User with this email already exists")
        }
        val user = User(
            cognitoSub = cognitoSub,
            email = request.email,
            name = request.name,
            role = request.role
        )
        val saved = userRepository.save(user)
        return UserDto(saved.id, saved.cognitoSub, saved.email, saved.name, saved.role)
    }

    fun getCurrentUser(cognitoSub: String): UserDto {
        val user = userRepository.findByCognitoSub(cognitoSub)
            .orElseThrow { RuntimeException("User not found") }
        return UserDto(user.id, user.cognitoSub, user.email, user.name, user.role)
    }
}

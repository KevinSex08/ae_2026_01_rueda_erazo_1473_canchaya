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

    fun getCurrentUser(jwt: org.springframework.security.oauth2.jwt.Jwt): UserDto {
        val cognitoSub = jwt.subject!!
        val userOptional = userRepository.findByCognitoSub(cognitoSub)
        
        val user = if (userOptional.isPresent) {
            val existingUser = userOptional.get()
            val tokenName = jwt.getClaimAsString("name") 
                ?: jwt.getClaimAsString("given_name") 
                ?: jwt.getClaimAsString("email")?.substringBefore("@")
                ?: existingUser.name
                
            // Actualiza el nombre si en la base de datos estaba como un UUID o es diferente
            if (existingUser.name != tokenName) {
                existingUser.name = tokenName
                userRepository.save(existingUser)
            } else {
                existingUser
            }
        } else {
            // Auto-registro silencioso (First-time login)
            val email = jwt.getClaimAsString("email") ?: "no-email@canchaya.com"
            // Extraer el nombre real (name) o usar el prefijo del correo como fallback
            val username = jwt.getClaimAsString("name") 
                ?: jwt.getClaimAsString("given_name") 
                ?: email.substringBefore("@")
            // Asignar rol basado en los grupos de cognito
            val groups = jwt.getClaimAsStringList("cognito:groups") ?: emptyList()
            val role = if (groups.contains("ADMIN")) "ADMIN" else "PLAYER"
            
            val newUser = User(
                cognitoSub = cognitoSub,
                email = email,
                name = username,
                role = role
            )
            userRepository.save(newUser)
        }
        
        return UserDto(user.id, user.cognitoSub, user.email, user.name, user.role)
    }
}

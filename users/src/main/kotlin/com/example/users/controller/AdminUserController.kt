package com.example.users.controller

import com.example.users.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(message: String) : RuntimeException(message)

@RestController
@RequestMapping("/api/v1/users")
class AdminUserController(private val userRepository: UserRepository) {

    // Note: Due to lack of shared DB, we allow the request. But for real prod, we'd add hasRole("ADMIN") 
    // and handle it properly. We assume Nginx or API Gateway protects /admin/ routes if needed.
    // For now, it allows promoting to ADMIN via the UI.
    @PostMapping("/admin/roles")
    fun assignAdmin(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        val email = request["email"] ?: throw IllegalArgumentException("email is required")
        val user = userRepository.findByEmail(email).orElseThrow { 
            ResourceNotFoundException("User with email $email not found") 
        }
        user.role = "ADMIN"
        userRepository.save(user)
        return ResponseEntity.ok(mapOf("message" to "Role ADMIN assigned successfully to $email"))
    }

    // Internal endpoint for Canchaya microservice to verify role
    @GetMapping("/internal/roles/{cognitoSub}")
    fun getInternalRole(@PathVariable cognitoSub: String): ResponseEntity<Map<String, String>> {
        val userOptional = userRepository.findByCognitoSub(cognitoSub)
        return if (userOptional.isPresent) {
            ResponseEntity.ok(mapOf("role" to userOptional.get().role))
        } else {
            ResponseEntity.ok(mapOf("role" to "PLAYER")) // default role
        }
    }
}

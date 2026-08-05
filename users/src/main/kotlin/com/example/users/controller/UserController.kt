package com.example.users.controller

import com.example.users.dto.UserDto
import com.example.users.dto.UserRequest
import com.example.users.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@AuthenticationPrincipal jwt: Jwt, @RequestBody request: UserRequest): UserDto {
        val cognitoSub = jwt.subject
        return userService.registerUser(cognitoSub, request)
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal jwt: Jwt): UserDto {
        val cognitoSub = jwt.subject
        return userService.getCurrentUser(cognitoSub)
    }
}

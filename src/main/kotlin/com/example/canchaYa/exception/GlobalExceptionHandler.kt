package com.example.canchaya.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message ?: "Not Found", LocalDateTime.now()), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.message ?: "Unauthorized", LocalDateTime.now()), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.message ?: "Forbidden", LocalDateTime.now()), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity(ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors, LocalDateTime.now()), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        // Prevent stacktrace leak (500)
        return ResponseEntity(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error. Please contact support.", LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: LocalDateTime
)

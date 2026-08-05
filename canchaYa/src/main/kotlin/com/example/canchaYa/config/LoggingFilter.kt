package com.example.canchaYa.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class LoggingFilter : Filter {

    private val log = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        try {
            val authentication = SecurityContextHolder.getContext().authentication
            val sub = if (authentication != null && authentication.principal is Jwt) {
                (authentication.principal as Jwt).subject
            } else {
                "anonimo"
            }
            MDC.put("sub", sub)

            log.info("event=http.request | msg={} {}", httpRequest.method, httpRequest.requestURI)
            
            chain.doFilter(request, response)
            
            log.info("event=http.response | msg={} {} {}", httpResponse.status, httpRequest.method, httpRequest.requestURI)
        } finally {
            MDC.remove("sub")
        }
    }
}

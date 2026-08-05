package com.example.canchaYa.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationConverter: JwtAuthenticationConverter
    ): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                // Courts endpoints - Corregido para aceptar la ruta exacta y subrutas
                auth.requestMatchers(HttpMethod.GET, "/api/v1/courts", "/api/v1/courts/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/v1/courts").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/courts/*").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v1/courts/*").hasRole("ADMIN")

                // Slots endpoints - Corregido para aceptar la ruta exacta y subrutas
                auth.requestMatchers(HttpMethod.GET, "/api/v1/slots", "/api/v1/slots/**").permitAll()
                auth.requestMatchers(HttpMethod.POST, "/api/v1/slots").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/api/v1/slots/*").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v1/slots/*").hasRole("ADMIN")

                // Reservations endpoints
                auth.requestMatchers(HttpMethod.POST, "/api/v1/reservations").hasRole("PLAYER")
                auth.requestMatchers(HttpMethod.GET, "/api/v1/reservations/my").hasRole("PLAYER")
                auth.requestMatchers(HttpMethod.GET, "/api/v1/reservations/*").authenticated()
                auth.requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/cancel").hasRole("PLAYER")
                auth.requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/confirm").hasRole("ADMIN")

                // Game Records endpoints
                auth.requestMatchers(HttpMethod.POST, "/api/v1/game-records").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.GET, "/api/v1/game-records/*").hasRole("PLAYER")
                auth.requestMatchers(HttpMethod.PATCH, "/api/v1/game-records/*/start").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.PATCH, "/api/v1/game-records/*/finish").hasRole("ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/api/v1/game-records/*").hasRole("ADMIN")

                // Players endpoints
                auth.requestMatchers("/api/v1/game-records/*/players").hasRole("PLAYER")
                auth.requestMatchers("/api/v1/players/*").hasRole("PLAYER")

                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                }
            }

        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        // Cognito groups or custom claims to Spring roles
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
        grantedAuthoritiesConverter.setAuthoritiesClaimName("cognito:groups") // Usually Cognito puts roles in groups

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    // THIS IS FOR TESTING PURPOSES ONLY.
    // It accepts ANY JWT token (even fake ones) for easy Postman testing without a real Cognito instance.
    // In a real environment, you would just rely on the `spring.security.oauth2.resourceserver.jwt.issuer-uri`
    // @Bean
    // fun customJwtDecoder(): JwtDecoder {
    //     return JwtDecoder { token ->
    //         val parts = token.split(".")
    //         if (parts.size != 3) throw RuntimeException("Invalid JWT")
    //
    //         val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
    //         val mapper = com.fasterxml.jackson.databind.ObjectMapper()
    //         val claims = mapper.readValue(payload, Map::class.java) as Map<String, Any>
    //
    //         Jwt.withTokenValue(token)
    //             .header("alg", "none")
    //             .claims { it.putAll(claims) }
    //             .build()
    //     }
    // }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        val frontendUrl = System.getenv("FRONTEND_URL") ?: "http://localhost:3000"
        configuration.allowedOrigins = listOf(frontendUrl, "http://localhost:8080") // Strict origins
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

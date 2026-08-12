package com.example.canchaYa.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
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
        http: HttpSecurity
    ): SecurityFilterChain {
        http
            .cors { it.disable() }
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
                    jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter())
                }
            }

        return http.build()
    }

    @Bean
    fun customJwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        return Converter { jwt ->
            val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
            grantedAuthoritiesConverter.setAuthoritiesClaimName("cognito:groups")

            val authorities = grantedAuthoritiesConverter.convert(jwt)?.toMutableList() ?: mutableListOf()

            // Si el usuario no tiene grupos en Cognito, le asignamos ROLE_PLAYER por defecto
            // para que pueda acceder a /api/v1/reservations/my sin configuraciones extra en AWS.
            val hasRoles = authorities.any { it.authority?.startsWith("ROLE_") == true }
            if (!hasRoles) {
                authorities.add(SimpleGrantedAuthority("ROLE_PLAYER"))
            }

            JwtAuthenticationToken(jwt, authorities)
        }
    }

}

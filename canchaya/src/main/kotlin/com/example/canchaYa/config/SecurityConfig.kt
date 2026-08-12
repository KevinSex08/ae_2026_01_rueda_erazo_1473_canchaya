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
    fun restTemplate(): org.springframework.web.client.RestTemplate {
        return org.springframework.web.client.RestTemplate()
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                // Permitir peticiones OPTIONS (Preflight) a cualquier ruta
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Admin endpoints (rutas específicas)
                auth.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

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
                    jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter(restTemplate()))
                }
            }

        return http.build()
    }

    @Bean
    fun customJwtAuthenticationConverter(restTemplate: org.springframework.web.client.RestTemplate): Converter<Jwt, AbstractAuthenticationToken> {
        return Converter { jwt ->
            val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
            grantedAuthoritiesConverter.setAuthoritiesClaimName("cognito:groups")

            val authorities = grantedAuthoritiesConverter.convert(jwt)?.toMutableList() ?: mutableListOf()

            try {
                val cognitoSub = jwt.subject
                val url = "http://users:8081/api/v1/users/internal/roles/$cognitoSub"
                val response = restTemplate.getForEntity(url, Map::class.java)
                if (response.statusCode.is2xxSuccessful) {
                    val dbRole = response.body?.get("role") as? String
                    if (dbRole == "ADMIN") {
                        authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
                    } else {
                        authorities.add(SimpleGrantedAuthority("ROLE_PLAYER"))
                    }
                } else {
                    authorities.add(SimpleGrantedAuthority("ROLE_PLAYER"))
                }
            } catch (e: Exception) {
                // Fail graceful to PLAYER
                authorities.add(SimpleGrantedAuthority("ROLE_PLAYER"))
            }

            JwtAuthenticationToken(jwt, authorities)
        }
    }

}

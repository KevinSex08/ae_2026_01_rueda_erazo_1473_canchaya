# ADR 001: Adopción de Arquitectura de Microservicios para el Backend

**1. Estado**
Aceptado y aplicado en producción.

**2. Contexto**
El sistema CanchaYa debe manejar dos dominios de negocio claramente diferenciados: la gestión operativa (Canchas, Horarios, Reservas, Resultados) y la gestión de identidades/perfiles (Usuarios, Autenticación). Utilizar una arquitectura monolítica clásica podría generar un Alto Acoplamiento Vertical, donde cambios en la lógica de usuarios afecten el despliegue del sistema crítico de reservas, reduciendo la disponibilidad.

**3. Decisión**
Se decidió fragmentar el backend mediante una **Arquitectura de Microservicios**. Se construyeron dos servicios autónomos usando Kotlin y Spring Boot 3:
- **Microservicio `canchaya`**: Gestiona el dominio core (Courts, Slots, Reservations, GameRecords).
- **Microservicio `users`**: Encapsula el dominio de identidad y perfilamiento.
Cada servicio posee su propia base de datos PostgreSQL independiente, garantizando el Encapsulamiento por Procesos.

**4. Consecuencias**
- **Positivas:** Alto nivel de Cohesión Funcional (cada servicio hace una sola cosa bien) y Bajo Acoplamiento Extremo. Escalabilidad independiente; si el módulo de reservas sufre alta demanda, se puede escalar sin afectar al de usuarios.
- **Negativas:** Aumenta la complejidad operativa y de despliegue. Se requirió orquestación adicional (Docker Compose) y gestión de red interna para la comunicación.

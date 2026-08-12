# ADR 006: Uso de Ionic Framework y React para el Frontend Multiplataforma

**1. Estado**
Aceptado.

**2. Contexto**
CanchaYa requería interfaces de usuario tanto para jugadores (enfocado a dispositivos móviles) como para administradores (enfocado a web/desktop). Desarrollar aplicaciones nativas separadas (Swift para iOS, Kotlin para Android) y una web (React) triplicaría los costos y tiempos de desarrollo.

**3. Decisión**
Se eligió la pila tecnológica **Ionic Framework + React (TypeScript)** compilado con Vite. Esto permite tener una única base de código (Single Codebase) que se adapta responsivamente tanto a navegadores web de escritorio (Panel Admin) como a aplicaciones PWA/Móviles (App de Jugadores).

**4. Consecuencias**
- **Positivas:** Máxima velocidad de desarrollo y mantenimiento unificado. Uso de componentes pre-estilizados de Ionic que garantizan una experiencia "Mobile-First" fluida y nativa.
- **Negativas:** Ligero sacrificio en el rendimiento extremo comparado con código nativo puro, lo cual es totalmente aceptable dado que la aplicación es de gestión transaccional (formularios, listas) y no requiere procesamiento gráfico intensivo.

# ADR 005: Control Estricto de Zona Horaria (Timezone) en Filtros de Disponibilidad

**1. Estado**
Aceptado y parcheado en producción.

**2. Contexto**
El sistema está desplegado en AWS (Ubuntu EC2), cuyos servidores operan de forma predeterminada bajo la zona horaria UTC (Inglaterra). CanchaYa opera en Ecuador (UTC-5). Cuando la lógica de negocio filtraba los horarios pasados (`it.startTime.isAfter(LocalDateTime.now())`), el reloj del servidor adelantaba 5 horas el tiempo real, ocultando canchas válidas a los jugadores (falsos positivos).

**3. Decisión**
Se forzó a nivel de código la inyección del contexto geográfico. En lugar de depender del reloj interno del sistema operativo, el servicio `SlotService` evalúa explícitamente el tiempo instanciando la zona `ZoneId.of("America/Guayaquil")`.

**4. Consecuencias**
- **Positivas:** Precisión cronológica absoluta independientemente de la configuración del servidor en la nube, solucionando el bug crítico de disponibilidad.
- **Negativas:** Acopla la lógica de negocio a una ubicación geográfica específica (Ecuador). Si el modelo de negocio se expande internacionalmente (ej. franquicias en Europa), el código requerirá una refactorización para manejar zonas horarias dinámicas por sede.

# ADR 004: Implementación de Idempotencia en API de Resultados (Game Records)

**1. Estado**
Aceptado y desplegado.

**2. Contexto**
En el panel del administrador ("Súper 8 / Marcador"), detectamos que si el usuario hacía doble clic en el botón de "Finalizar Partido" o si React re-renderizaba el componente, se enviaban múltiples peticiones POST simultáneas a `/api/v1/game-records`. Esto provocaba bloqueos `HTTP 500` porque Hibernate intentaba violar la restricción de base de datos de "un marcador por reserva".

**3. Decisión**
Se implementó **Idempotencia** en el servicio de `GameRecordService`. Antes de intentar insertar un nuevo marcador en la base de datos, el backend consulta si ya existe un registro asociado al `reservationId`. Si ya existe, aborta la creación silenciosamente y retorna el registro existente con código `200 OK` en lugar de fallar.

**4. Consecuencias**
- **Positivas:** El backend se vuelve resiliente ante comportamientos erráticos de la red o del cliente (Tolerancia a fallos). Mejora drásticamente la experiencia del administrador al no mostrar errores técnicos incomprensibles en pantalla.
- **Negativas:** Agrega una consulta extra de lectura (`SELECT`) a la base de datos por cada intento de escritura (`INSERT`), impactando marginalmente el tiempo de respuesta.

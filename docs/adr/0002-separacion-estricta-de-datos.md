# ADR 002: Separación Estricta de Datos y Comunicación Inter-Servicios

**1. Estado**
Aceptado y aplicado en la versión actual.

**2. Contexto**
Bajo el paradigma de microservicios, el microservicio `canchaya` necesita saber a quién pertenece una reserva, pero no debe acceder directamente a la base de datos de usuarios para mantener el desacoplamiento técnico. Durante la creación del panel de administrador, el Frontend necesitaba mostrar el nombre real del jugador junto a su reserva.

**3. Decisión**
En lugar de duplicar los datos (nombre, email) en la base de datos de reservas, el microservicio `canchaya` únicamente almacena el `cognitoUserId` (identificador único inmutable). Para resolver el cruce de datos en la interfaz, se expuso un endpoint global (`GET /api/v1/users/all`) en el servicio de usuarios. Es el **Frontend** quien se encarga de hacer el 'join' o cruce de datos en tiempo de ejecución, mapeando el `cognitoUserId` con el nombre real del jugador.

**4. Consecuencias**
- **Positivas:** Se mantiene la integridad de la arquitectura, asegurando que el microservicio de reservas sea "agnóstico" a los detalles personales del usuario. Evita cuellos de botella de red entre microservicios en el backend.
- **Negativas:** El Frontend asume una mayor responsabilidad lógica al tener que consumir dos endpoints simultáneamente y cruzar los arreglos de datos en memoria (aumenta levemente el uso de memoria en el cliente).

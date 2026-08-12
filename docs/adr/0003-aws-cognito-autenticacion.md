# ADR 003: Uso de AWS Cognito y Auto-Registro Silencioso para la Autenticación

**1. Estado**
Aceptado y activo.

**2. Contexto**
El sistema requería un mecanismo seguro para autenticar jugadores y administradores. Implementar un servidor de autorización Oauth2/JWT desde cero introduciría riesgos de seguridad altos, un gran esfuerzo de desarrollo y mantenimiento (manejo de contraseñas, recuperación, etc.).

**3. Decisión**
Se decidió delegar la gestión de identidades a **AWS Cognito** (Identity ProvideraaS). El Frontend obtiene un JWT de Cognito y lo adjunta vía interceptores de Axios. En el backend (microservicio `users`), se implementó un patrón de **Auto-Registro Silencioso**: Spring Security valida el token criptográficamente mediante el `Issuer URI`. Si el JWT es válido pero el usuario no existe en la base de datos PostgreSQL local, el sistema extrae los *claims* (nombre, email, grupo) y lo registra automáticamente.

**4. Consecuencias**
- **Positivas:** Seguridad delegada a infraestructura de nivel empresarial (AWS). Acelera el desarrollo (Time-to-Market). El auto-registro asegura que la base de datos local y Cognito estén siempre sincronizados sin fricción para el usuario.
- **Negativas:** Dependencia de un proveedor externo (Vendor Lock-in). El sistema no puede autenticar usuarios si los servidores de AWS Cognito sufren una caída.

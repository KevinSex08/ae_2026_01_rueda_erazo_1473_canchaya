# CanchaYa - Backend & Infraestructura

Aplicación para la gestión de reservas de canchas y partidos de fútbol/padel (Traditional y Super 8), construida con **Spring Boot + Kotlin** y orquestada con **Docker Compose**.

## 📂 Estructura General del Proyecto

Esta estructura está organizada de manera modular para separar la infraestructura del código fuente de la aplicación:

```text
CanchaYa/ (Raíz)
│
├── 📂 docs/                     <-- Documentación detallada del proyecto
│   └── ESTRUCTURA_Y_EXPLICACION.md
│
├── 📂 canchaYa/                 <-- Código fuente y construcción del backend (Spring Boot + Kotlin)
│   ├── src/
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradlew
│   ├── gradlew.bat
│   └── Dockerfile
│
├── 📂 nginx/                    <-- Configuración del proxy inverso para el acceso externo
│   └── nginx.conf
│
├── 📂 pgadmin/                  <-- Carpeta reservada para administración de base de datos
│
├── 📂 postman/                  <-- Colección de endpoints lista para importar y probar
│   └── CanchaYa.postman_collection.json
│
├── 📄 .env.example              <-- Plantilla de variables de entorno para desarrollo local
├── 📄 .gitattributes            <-- Reglas de control de línea de Git
├── 📄 .gitignore                <-- Reglas de exclusión de Git a nivel raíz
├── 📄 README.md                 <-- Guía de inicio rápido (este archivo)
└── 📄 docker-compose.yml        <-- Orquestación de servicios locales
```

---

## 🚀 Requisitos Previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución.
- [Postman](https://www.postman.com/) para probar los endpoints.
- Java JDK 17 o superior (opcional, solo para desarrollo local sin Docker).

---

## 🛠️ Configuración e Inicio

### 1. Variables de Entorno
Copia el archivo de plantilla `.env.example` y renómbralo a `.env`:
```bash
cp .env.example .env
```
Puedes editar este archivo `.env` para ajustar las credenciales de la base de datos si lo consideras necesario.

### 2. Levantar la Aplicación con Docker Compose
Desde la raíz del proyecto, ejecuta el siguiente comando:
```bash
docker compose up --build -d
```
Esto levantará los siguientes servicios de forma automática:
- **postgres_db**: Base de datos relacional PostgreSQL expuesta en el puerto parametrizado (por defecto `5432`).
- **backend**: Aplicación de Spring Boot compilando y corriendo de forma aislada en el puerto interno `8080`.
- **nginx**: Proxy inverso actuando de gateway en el puerto `80` para recibir y redirigir tráfico al backend de forma transparente.

Para apagar los servicios:
```bash
docker compose down
```

---

## 🧪 Pruebas de la API (Postman)

En la carpeta [postman/](postman/), se ha incluido el archivo **`CanchaYa.postman_collection.json`**.
1. Abre **Postman**.
2. Haz clic en **Import**.
3. Selecciona el archivo `postman/CanchaYa.postman_collection.json`.
4. ¡Listo! Ya tienes todos los endpoints documentados y listos para ejecutar con variables preconfiguradas.

---

## 💻 Desarrollo Local (Sin Docker)

Si prefieres correr la aplicación directamente en tu máquina:
1. Asegúrate de tener una base de datos PostgreSQL activa con las credenciales que se detallan en el archivo `canchaYa/src/main/resources/application.yaml`.
2. Dirígete a la carpeta `canchaYa/`:
   ```bash
   cd canchaYa
   ```
3. Compila y ejecuta los tests de integración:
   ```bash
   ./gradlew test
   ```
4. Inicia la aplicación:
   ```bash
   ./gradlew bootRun
   ```

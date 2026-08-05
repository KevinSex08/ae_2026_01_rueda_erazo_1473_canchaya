# Catálogo Detallado y Estructura Completa del Proyecto canchaYA

Este documento contiene la explicación exhaustiva de **cada uno** de los archivos y directorios que componen la arquitectura modular y de microservicios de la aplicación **canchaYA**. Se detalla la ubicación de cada módulo, su propósito específico y cómo interactúan los componentes dentro del sistema, cubriendo la infraestructura de contenedores, el proxy inverso y los servicios basados en Spring Boot + Kotlin.

---

## 📂 Estructura General del Proyecto (Multiservicio)

El repositorio se ha reorganizado en una estructura modular multiservicio para separar las responsabilidades de negocio, el perfilado de usuarios y la infraestructura.

A continuación se muestra el árbol de directorios del proyecto:

```text
CanchaYa/ (Raíz del Repositorio)
│
├── ⚙️ CONFIGURACIÓN E INFRAESTRUCTURA GLOBAL
│   ├── .env                             <-- Variables de entorno locales (DB, CORS, etc.)
│   ├── docker-compose.yml               <-- Orquestación de contenedores (DB, Backend, Nginx)
│   ├── .gitattributes                   <-- Configuración de atributos de fin de línea
│   └── .gitignore                       <-- Exclusiones de Git a nivel raíz
│
├── 📂 docs/                             <-- Documentación técnica
│   └── ESTRUCTURA_Y_EXPLICACION.md      <-- Este archivo explicativo
│
├── 📂 nginx/                            <-- Configuración del Gateway / Proxy Inverso
│   └── nginx.conf                       <-- Reglas de ruteo de Nginx a los backends
│
├── 📂 postman/                          <-- Colecciones de pruebas API
│   └── CanchaYa.postman_collection.json <-- Colección Postman para el servicio canchaYa
│
├── 📂 canchaYa/                         <-- SERVICIO PRINCIPAL (Canchas, Reservas y Partidos)
│   ├── Dockerfile                       <-- Construcción de la imagen Docker para canchaYa
│   ├── build.gradle.kts                 <-- Dependencias y compilación de canchaYa
│   ├── settings.gradle.kts              <-- Nombre del subproyecto
│   ├── gradlew / gradlew.bat            <-- Wrapper de Gradle
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/example/canchaYa/
│   │   │   │   ├── CanchaYaApplication.kt (Punto de entrada)
│   │   │   │   ├── config/              <-- Seguridad de Cognito y semillero (Seed) de datos
│   │   │   │   ├── controller/          <-- Controladores REST
│   │   │   │   ├── dto/                 <-- Objetos de transferencia de datos
│   │   │   │   ├── entity/              <-- Entidades JPA y Enums de negocio
│   │   │   │   ├── exception/           <-- Manejo global de excepciones
│   │   │   │   ├── mapper/              <-- Mapeadores Entity <-> DTO
│   │   │   │   ├── repository/          <-- Repositorios JPA (PostgreSQL)
│   │   │   │   └── service/             <-- Lógica transaccional de reservas y partidos
│   │   │   └── resources/
│   │   │       └── application.yaml     <-- Configuración de base de datos Postgres y Cognito
│   │   └── test/
│   │       ├── kotlin/com/example/canchaYa/
│   │       │   ├── CanchaYaApplicationTests.kt
│   │       │   └── service/
│   │       │       └── ReservationAndGameRecordServiceTest.kt <-- Pruebas de reglas críticas
│   │       └── resources/
│   │           └── application-test.yaml <-- Configuración de base de datos H2 de pruebas
│   └── build/                           <-- Generados del build local
│
└── 📂 users/                            <-- MICROSERVICIO DE PERFILES DE USUARIOS
    ├── build.gradle.kts                 <-- Dependencias y compilación del microservicio users
    ├── settings.gradle.kts              <-- Nombre del subproyecto users
    ├── gradlew / gradlew.bat            <-- Wrapper de Gradle para users
    ├── guia_microservicio_users.md      <-- Guía específica del microservicio users
    ├── users.postman_collection.json    <-- Colección Postman para probar el microservicio users
    ├── src/
    │   ├── main/
    │   │   ├── kotlin/com/pucetec/users/
    │   │   │   ├── UsersApplication.kt  <-- Punto de entrada del microservicio users
    │   │   │   ├── config/              <-- Configuración de seguridad (Cognito Resource Server)
    │   │   │   ├── controllers/         <-- Endpoints de perfiles y administración
    │   │   │   ├── dto/                 <-- DTOs de entrada y salida
    │   │   │   ├── entities/            <-- Modelo de persistencia User (CognitoId ↔ Perfil)
    │   │   │   ├── exceptions/          <-- Control de excepciones específicas
    │   │   │   ├── mappers/             <-- Conversión entre entidades y DTOs
    │   │   │   ├── repositories/        <-- Acceso a base de datos de usuarios
    │   │   │   └── services/            <-- Lógica de negocio de perfiles de usuario
    │   │   └── resources/
    │   │       └── application.yaml     <-- Configuración del microservicio users (H2 & Cognito)
    │   └── test/                        <-- Pruebas de perfiles e integraciones
```

---

## 🛠️ 1. Configuración Global e Infraestructura

### 📄 [docker-compose.yml](file:///c:/Users/LENOVO/Desktop/CanchaYa/docker-compose.yml)
* **Propósito**: Levantar y orquestar los contenedores del entorno local en una red común (`canchaya_network`).
* **Servicios configurados**:
  - `postgres_db`: Base de datos PostgreSQL en el puerto `5432` con volumen persistente `canchaya_data`.
  - `backend`: Levanta el backend principal `canchaYa` construyendo su `Dockerfile` local. Depende de la base de datos y se conecta mediante variables de entorno especificadas en el archivo [`.env`](file:///c:/Users/LENOVO/Desktop/CanchaYa/.env).
  - `nginx`: Expone el puerto `80` al host de la máquina, mapeando la configuración definida en [nginx.conf](file:///c:/Users/LENOVO/Desktop/CanchaYa/nginx/nginx.conf) para actuar como Gateway.

### 📄 [nginx.conf](file:///c:/Users/LENOVO/Desktop/CanchaYa/nginx/nginx.conf)
* **Propósito**: Proxy inverso para rutear peticiones HTTP entrantes.
* **Detalle**:
  - Redirige las solicitudes HTTP externas a los servicios internos del contenedor (por ahora mapeado principalmente a `backend:8080`).
  - Inyecta cabeceras HTTP de proxy (`X-Real-IP`, `X-Forwarded-For`, `X-Forwarded-Proto`) para asegurar que el backend reciba correctamente la información de red del origen del cliente.

### 📄 [.env](file:///c:/Users/LENOVO/Desktop/CanchaYa/.env)
* **Propósito**: Archivo local de variables de entorno para que Docker Compose configure las credenciales de PostgreSQL (`DB_USER`, `DB_PASSWORD`, `DB_NAME`, `DB_PORT`, `DB_HOST`) y URLs de configuración como `FRONTEND_URL` para configurar los orígenes permitidos en CORS.

---

## ⚽ 2. Servicio Principal `canchaYa` (Gestión Deportiva)

Ubicado en la carpeta [`canchaYa/`](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa). Maneja el núcleo operativo del sistema: canchas, turnos de reserva, marcadores y registro de jugadores.

### ⚙️ Configuración del Módulo
* **[build.gradle.kts](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/build.gradle.kts)**: Declara dependencias para PostgreSQL, Hibernate JPA, Spring Security OAuth2 Resource Server para verificar JWTs de Cognito, y H2 para testing local.
* **[application.yaml](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/resources/application.yaml)**: Configura el puerto local `8080`, la conexión PostgreSQL dinámica, y el emisor JWT de Cognito (`issuer-uri`) del pool correspondiente al dominio del negocio.

### 📌 Dominio y Entidades ([entity/](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity))
* **[BaseEntity.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/BaseEntity.kt)**: Define los atributos globales de auditoría: `createdAt` (marca de tiempo por defecto en Postgres) y `deletedAt` (borrado lógico/soft delete).
* **[Court.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/Court.kt)**: Representa la cancha física (`name`, `isIndoor`).
* **[Slot.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/Slot.kt)**: Intervalos de tiempo disponibles para alquilar. Posee relación con `Court` e indica horario de inicio, fin y precio del turno.
* **[Reservation.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/Reservation.kt)**: Almacena la reserva de slots. Contiene `cognitoUserId` (quien reserva), tipo de partido (`GameType`), estado de reserva (`ReservationStatus`), y soporta hasta 2 slots asociados para el modo `SUPER_8`.
* **[GameRecord.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/GameRecord.kt)**: Planilla del juego relacionada 1:1 con la reserva. Almacena las marcas de tiempo reales de juego, marcadores (`teamAScore`, `teamBScore`) y el equipo ganador.
* **[Player.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/entity/Player.kt)**: Jugador inscrito en un partido específico, asignado a un equipo (`Team.TEAM_A`, `Team.TEAM_B`).

### 📌 Lógica de Negocio y Reglas de Validación
* **[ReservationService.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/service/ReservationService.kt)**:
  - **`TRADITIONAL`**: Requiere exactamente **1 slot**.
  - **`SUPER_8`**: Requiere exactamente **2 slots consecutivos** de la misma cancha.
  - Valida solapamientos de turnos y disponibilidad.
* **[PlayerService.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/main/kotlin/com/example/canchaYa/service/PlayerService.kt)**:
  - Restringe el cupo de jugadores:
    - Máximo **4 jugadores** en total (2 por equipo) para partidos de tipo `TRADITIONAL`.
    - Máximo **8 jugadores** en total (4 por equipo) para partidos de tipo `SUPER_8`.
  - Impide la asignación a `Team.NONE` (el jugador debe pertenecer obligatoriamente a un equipo competidor).

### 🧪 Pruebas Automatizadas
* **[ReservationAndGameRecordServiceTest.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/test/kotlin/com/example/canchaYa/service/ReservationAndGameRecordServiceTest.kt)**: Prueba de integración robusta que simula los límites de jugadores, validación de turnos consecutivos para Super 8 y la prevención de registros de turnos ya ocupados en la base de datos de pruebas H2 en memoria ([application-test.yaml](file:///c:/Users/LENOVO/Desktop/CanchaYa/canchaYa/src/test/resources/application-test.yaml)).

---

## 👤 3. Microservicio `users` (Perfilado y Datos Propios)

Ubicado en la carpeta [`users/`](file:///c:/Users/LENOVO/Desktop/CanchaYa/users). Nació con una **responsabilidad única**: resolver la brecha entre la autenticación de AWS Cognito y la lógica interna de la aplicación.

### 💡 Concepto de Diseño Clave
AWS Cognito autentica a los usuarios a nivel global y expone un identificador único descentralizado (`sub` o `cognitoId`). Sin embargo, Cognito no almacena datos locales enriquecidos como el teléfono o nombres estructurados de negocio.
Este microservicio asocia ese ID único (`sub` extraído directamente de la validación criptográfica del JWT de Cognito) a los perfiles de datos en nuestra base de datos local.

> [!IMPORTANT]
> **Seguridad y Extracción de Contexto:** El cliente web **nunca** proporciona el `cognitoId` en el cuerpo (body) de las peticiones para crear o modificar su perfil personal. El backend extrae el id desde la cabecera `Authorization` procesando el JWT mediante Spring Security:
> `val cognitoId = jwt.subject` (donde `subject` mapea al `sub` del token). Esto impide la suplantación de identidad.

### ⚙️ Componentes de Código en `users`
* **[User.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/users/src/main/kotlin/com/pucetec/users/entities/User.kt)**: Entidad que almacena los datos básicos (`id`, `cognitoId` único indexado, `name`, `email`, `phone`).
* **[UserController.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/users/src/main/kotlin/com/pucetec/users/controllers/UserController.kt)**:
  - `POST /api/users/me`: Permite al usuario logueado registrar su propio perfil.
  - `GET /api/users/me`: Recupera el perfil del usuario autenticado.
  - `PUT /api/users/me`: Actualiza los datos del perfil actual.
  - `GET /api/users/cognito/{cognitoId}`: Permite a **otros microservicios** (como canchaYa) traducir de forma interna un ID de Cognito a datos enriquecidos.
  - `GET /api/users` & `GET /api/users/{id}` & `DELETE /api/users/{id}`: Métodos administrativos.
* **[UserService.kt](file:///c:/Users/LENOVO/Desktop/CanchaYa/users/src/main/kotlin/com/pucetec/users/services/UserService.kt)**: Valida la unicidad del `cognitoId` (no permite perfiles duplicados) y aplica reglas como nombres no vacíos.
* **[application.yaml](file:///c:/Users/LENOVO/Desktop/CanchaYa/users/src/main/resources/application.yaml)**: Configura el puerto local `8686`, usa una base de datos ligera H2 en memoria y apunta al pool de Cognito específico para perfiles.

---

## 🚀 Guía de Ejecución y Pruebas del Ecosistema

### A. Ejecutar Todo el Ecosistema mediante Docker Compose
Este es el flujo recomendado para despliegues o pruebas globales:
1. Asegúrate de que el archivo [`.env`](file:///c:/Users/LENOVO/Desktop/CanchaYa/.env) esté creado en la raíz.
2. Desde la terminal en el directorio raíz de `CanchaYa`, corre:
   ```bash
   docker compose up --build -d
   ```
3. Esto compilará el código de `canchaYa`, creará el esquema de PostgreSQL y levantará Nginx en el puerto `80`.

### B. Ejecutar de Forma Individual en Desarrollo Local (Sin Docker)
Para iterar de manera rápida en desarrollo, puedes ejecutar los proyectos usando Gradle localmente:

* **Servicio Principal `canchaYa`**:
  ```bash
  cd canchaYa
  ./gradlew bootRun
  ```
  *La aplicación estará activa en: `http://localhost:8080`*

* **Microservicio `users`**:
  ```bash
  cd users
  ./gradlew bootRun
  ```
  *La aplicación estará activa en: `http://localhost:8686`*
  *Consola de base de datos H2 en: `http://localhost:8686/h2-console` (JDBC URL: `jdbc:h2:mem:usersdb`, User: `admin`, Pass: `admin`)*

---

## 🧪 Pruebas y Verificación

Cada módulo posee su respectiva capa de pruebas unitarias y de integración que se ejecutan automáticamente durante la integración continua o de forma local:
- Para `canchaYa`: `cd canchaYa && ./gradlew test`
- Para `users`: `cd users && ./gradlew test`

### Colecciones de Postman para Pruebas Manuales
* **CanchaYa Principal**: Importa [`postman/CanchaYa.postman_collection.json`](file:///c:/Users/LENOVO/Desktop/CanchaYa/postman/CanchaYa.postman_collection.json).
* **Perfiles de Usuarios**: Importa [`users/users.postman_collection.json`](file:///c:/Users/LENOVO/Desktop/CanchaYa/users/users.postman_collection.json) (también puedes pegarlo en tu cliente favorito).
  *Recuerda que para probar los endpoints protegidos `/api/**` debes adjuntar un JWT `access_token` válido obtenido desde la Hosted UI de tu respectivo pool de Cognito en las cabeceras de autorización (`Authorization: Bearer <TOKEN>`).*

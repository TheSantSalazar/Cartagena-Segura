# Cartagena Segura - Backend

API REST para gestion de incidentes de seguridad ciudadana en Cartagena.

El proyecto combina autenticacion JWT, gestion de incidentes y zonas, notificaciones, logs de auditoria, subida de evidencias y endpoints de apoyo con IA.

## Estado del proyecto

- Framework: Spring Boot 3.4.3
- Java: 21
- Build: Maven
- Version en `pom.xml`: `1.0.0`

## Funcionalidades principales

- Registro e inicio de sesion con JWT
- Reporte y seguimiento de incidentes
- Gestion de zonas con nivel de riesgo
- Comentarios en incidentes (publicos e internos)
- Notificaciones por usuario
- Logs de auditoria (solo ADMIN)
- Upload de archivos de evidencia
- Contactos de emergencia
- Endpoints de IA (`chat`, `classify`, `summary`, `zones/analysis`)

## Arquitectura de datos

El backend usa dos motores:

- MongoDB (Atlas): `incidents`, `zones`, `comments`, `logs`, `notifications`, `incident_history`, `reports`
- PostgreSQL (Supabase): `users`, `roles`, `emergency_contacts`

## Requisitos

- Java 21
- Maven 3.9+
- Instancia de PostgreSQL
- Instancia de MongoDB
- Cuenta/clave de Groq (para endpoints IA)
- Cuenta/clave de SendGrid (correo de bienvenida)

## Variables de entorno

Crea un archivo `.env` en la raiz (esta ignorado por Git) con:

```env
DB_URL=jdbc:postgresql://<host>:5432/<db>
DB_USERNAME=<usuario>
DB_PASSWORD=<password>

MONGO_URI=mongodb+srv://<usuario>:<password>@<cluster>/<db>?retryWrites=true&w=majority
MONGO_DATABASE=<nombre_db>

GROQ_API_KEY=<tu_api_key>

JWT_SECRET=<secreto_de_al_menos_32_caracteres>
JWT_EXPIRATION=86400000

SENDGRID_API_KEY=<tu_sendgrid_key>
MAIL_FROM=<correo_remitente>

UPLOAD_DIR=uploads
BASE_URL=http://localhost:8080
PORT=8080
```

## Ejecucion local

1. Instala dependencias y compila:

```bash
./mvnw clean package
```

2. Ejecuta la app:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

## Docker

Construir imagen:

```bash
docker build -t cartagena-segura-backend .
```

Ejecutar contenedor:

```bash
docker run --env-file .env -p 8080:8080 cartagena-segura-backend
```

## Documentacion API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Health (Actuator): `http://localhost:8080/actuator/health`

## Seguridad y acceso

Rutas publicas relevantes:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/files/**`
- `GET /api/emergency-contacts/**`
- Swagger y Actuator

Rutas protegidas:

- Todo lo demas requiere JWT
- Admin only: `/api/logs/**`, `/api/reports/**`, `/api/ai/summary`, `/api/ai/zones/analysis`

## Endpoints base

- `/api/auth`
- `/api/incidents`
- `/api/zones`
- `/api/incidents/{incidentId}/comments`
- `/api/notifications`
- `/api/files`
- `/api/emergency-contacts`
- `/api/logs`
- `/api/ai`

## Flujo rapido de autenticacion

1. Login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "tu_usuario",
  "password": "tu_password"
}
```

2. Usar token:

```http
Authorization: Bearer <jwt_token>
```

## Dashboard Power BI

Se agrego una guia paso a paso para analitica en:

- `docs/powerbi-dashboard-paso-a-paso.md`

Incluye consultas Power Query, modelo de datos y medidas DAX.

## Estructura del proyecto

```text
src/main/java/com/ProAula/Cartagena_Segura
  |- Controller
  |- Service
  |- Repository
  |- Model
  |- Security
  |- Dto
src/main/resources
  |- application.properties
  |- static/
  |- templates/
docs/
  |- powerbi-dashboard-paso-a-paso.md
```

## Notas

- El archivo `HELP.md` contiene referencias genericas de Spring.
- Este repositorio actualmente no incluye licencia explicita.


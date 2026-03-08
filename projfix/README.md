# Transport Passenger API

Backend reactivo listo para producción para un dominio de **transporte de pasajeros**, construido con **Java 17, Spring Boot 3, WebFlux, Spring Security, JWT, R2DBC, PostgreSQL, MySQL, Docker y docker-compose**.

## Arquitectura

### Fase 1 - Diseño

- **Auth Service**: registro, login y emisión de JWT.
- **ResourceA**: `Passenger` en PostgreSQL.
- **ResourceB**: `Trip` en MySQL.
- **Persistencia distribuida real**:
  - PostgreSQL: `users`, `resource_a`
  - MySQL: `resource_b`
- **Arquitectura hexagonal**:
  - `domain`: modelos y puertos
  - `application`: servicios y casos de uso
  - `infrastructure`: controladores, seguridad, configuración, adaptadores de salida
  - `shared`: DTOs, mappers, errores
- **Seguridad JWT reactiva**:
  - claims: `sub`, `role`, `iat`, `exp`
  - filtro WebFlux reactivo
  - roles traducidos a `ROLE_ADMIN` y `ROLE_USER`
- **Autorización**:
  - `/auth/register` y `/auth/login` públicos
  - `resourceA`
    - USER: GET + POST
    - ADMIN: CRUD
  - `resourceB`
    - USER: solo GET
    - ADMIN: CRUD

### Fase 2 - Estructura de carpetas

```text
src/main/java/com/example/app
├── domain
│   ├── model
│   └── ports
├── application
│   ├── services
│   └── usecases
├── infrastructure
│   ├── adapters
│   │   ├── input
│   │   └── output
│   ├── config
│   └── security
└── shared
    ├── dto
    ├── mapper
    └── exceptions
```

### Fase 3 - Dependencias Maven

Incluye exactamente:
- Spring Boot 3
- WebFlux
- Spring Security
- JWT (`jjwt`)
- Spring Data R2DBC
- `r2dbc-postgresql`
- `r2dbc-mysql`
- `r2dbc-pool`
- JUnit 5
- WebTestClient

### Fase 4 - Código

El proyecto ya viene completo, compilable, dockerizado, con tests y colección de Postman.

## Variables de entorno

```bash
APP_JWT_SECRET=ChangeThisSecretKeyForJwtSigning1234567890
APP_JWT_EXPIRATION_MINUTES=60

POSTGRES_HOST=postgres
POSTGRES_PORT=5432
POSTGRES_DB=transportdb
POSTGRES_USER=transport
POSTGRES_PASSWORD=transport

MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DB=transportdb
MYSQL_USER=transport
MYSQL_PASSWORD=transport
```

## Ejecución con Docker

```bash
docker-compose up --build
```

Servicios levantados:
- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- MySQL: `localhost:3306`

## Usuario administrador bootstrap

La aplicación crea automáticamente este usuario si no existe:

```text
email: admin@transport.local
password: Admin123!
role: ADMIN
```

## Ejecutar tests

```bash
mvn test
```

## Compilar

```bash
mvn clean install
```

## cURL

### Register

```bash
curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "username": "sofia",
  "email": "sofia@correo.com",
  "password": "Password123"
}'
```

### Login admin

```bash
curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "admin@transport.local",
  "password": "Admin123!"
}'
```

### Usar token

```bash
export TOKEN="<jwt>"
```

### Consultar pasajeros

```bash
curl --location 'http://localhost:8080/api/resourceA' \
--header "Authorization: Bearer $TOKEN"
```

### Crear pasajero

```bash
curl --location 'http://localhost:8080/api/resourceA' \
--header "Authorization: Bearer $TOKEN" \
--header 'Content-Type: application/json' \
--data-raw '{
  "fullName": "Ana Perez",
  "documentNumber": "CC123456",
  "email": "ana@correo.com",
  "status": "ACTIVE"
}'
```

### Crear viaje (solo ADMIN)

```bash
curl --location 'http://localhost:8080/api/resourceB' \
--header "Authorization: Bearer $TOKEN" \
--header 'Content-Type: application/json' \
--data-raw '{
  "code": "TR-100",
  "origin": "Bogota",
  "destination": "Medellin",
  "departureTime": "2030-01-01T08:00:00",
  "availableSeats": 40,
  "status": "SCHEDULED"
}'
```

## Respuesta de error global

```json
{
  "timestamp": "2026-03-06T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/api/resourceA"
}
```


## Nota importante para Docker

Los scripts `schema-postgres.sql` y `schema-mysql.sql` se cargan desde el classpath de Spring Boot (`src/main/resources`) por medio de R2DBC al iniciar la API. Por eso el `docker-compose.yml` no monta esos archivos en los contenedores de base de datos.

Si ya habías levantado una versión anterior del proyecto, limpia volúmenes antes de volver a iniciar:

```bash
docker compose down -v
docker compose up --build
```

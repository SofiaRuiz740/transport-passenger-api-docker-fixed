# Diseño Arquitectónico

## 1. Dominio

Tema: **Transporte - pasajero**

### Módulos funcionales
- **Auth Service**: registro, login, emisión de JWT.
- **ResourceA = Passenger**: gestión de pasajeros.
- **ResourceB = Trip**: gestión de viajes/rutas programadas.

## 2. Arquitectura Hexagonal
- **Domain**: modelos puros y puertos.
- **Application**: servicios de negocio que orquestan puertos.
- **Infrastructure**: controllers WebFlux, seguridad JWT, configuración, adaptadores R2DBC por motor.
- **Shared**: DTOs, mappers y manejo global de errores.

## 3. Persistencia distribuida real
- **PostgreSQL**
  - `users`
  - `resource_a`
- **MySQL**
  - `resource_b`

No hay duplicación de tablas, ni replicación, ni selección por perfil de una única base.

## 4. Seguridad reactiva
- JWT con claims: `sub`, `role`, `iat`, `exp`.
- `WebFilter` reactivo que:
  - lee `Authorization`
  - valida `Bearer`
  - valida firma y expiración
  - traduce el claim `role` a `ROLE_ADMIN` / `ROLE_USER`
  - inyecta `Authentication` en `ReactiveSecurityContext`

## 5. Reglas de autorización
- Públicos:
  - `POST /auth/register`
  - `POST /auth/login`
- `resourceA`
  - USER: GET, POST
  - ADMIN: CRUD completo
- `resourceB`
  - USER: solo GET
  - ADMIN: CRUD completo

## 6. Estrategia de infraestructura
- Dos `ConnectionFactory` reactivos.
- Dos pools R2DBC.
- Dos `R2dbcEntityTemplate`.
- Adaptadores separados por base de datos.
- Scripts `schema-postgres.sql` y `schema-mysql.sql` cargados al inicio.

## 7. Testing
- `WebFluxTest`
- `WebTestClient`
- validación de login, autorización por roles y endpoints protegidos.

docker ps
docker exec -it transport-postgres psql -U transport -d transportdb
docker exec -it transport-mysql mysql -uroot -p

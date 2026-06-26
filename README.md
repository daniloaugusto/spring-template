# Spring Template

[![CI](https://github.com/daniloaugusto/spring-template/actions/workflows/ci.yml/badge.svg)](https://github.com/daniloaugusto/spring-template/actions/workflows/ci.yml)

Spring Boot project template with hexagonal architecture, JWT authentication, and Liquibase migrations.

**Java 21 · Spring Boot 3.4.4 · Gradle Kotlin DSL · PostgreSQL**

---

## Stack

- **Java 21** — records, pattern matching, virtual threads
- **Spring Boot 3.4.4** — Web, Security, Data JPA, Validation
- **Gradle 8.12** — Kotlin DSL, dependency management
- **PostgreSQL 17** — database
- **Liquibase** — YAML-based schema migrations
- **Spring Security + JWT** — stateless authentication (jjwt)
- **Testcontainers** — real PostgreSQL for integration tests
- **Springdoc OpenAPI** — Swagger UI at `/swagger-ui.html`
- **Spring Boot Actuator** — health endpoint at `/actuator/health`
- **Spotless** — Google Java Format (./gradlew spotlessApply)
- **Checkstyle + PMD** — static analysis (./gradlew check)

---

## Architecture

```
┌────────────────────────────────────────────────┐
│            Web Layer (Controller)              │
│  infrastructure/web · infrastructure/security  │
├────────────────────────────────────────────────┤
│         Application Layer (DTOs)               │
│          application/dto/request               │
│          application/dto/response              │
├────────────────────────────────────────────────┤
│  Domain Layer (Model · Port · Service)         │
│  domain/model · domain/port · domain/service   │
├────────────────────────────────────────────────┤
│       Persistence Layer (JPA · Adapter)        │
│   infrastructure/persistence/entity            │
│   infrastructure/persistence/repository        │
│   infrastructure/persistence/adapter           │
└────────────────────────────────────────────────┘
```

### Request flow

```
POST /api/samples
  → SampleController
    → SampleUseCase (inbound port)
      → SampleService
        → SampleRepository (outbound port)
          → SampleRepositoryAdapter
            → SampleJpaRepository
              → PostgreSQL
```

Each layer depends only inward. The domain layer has no framework dependencies — pure Java interfaces and models. The persistence layer adapts JPA entities to domain models via port interfaces (hexagonal / clean architecture).

---

## Quick start

```bash
# Start PostgreSQL
docker compose -f infra/docker-compose.yml up -d

# Run the application
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> CORS is configured to allow all origins (`*`). Restrict `CorsConfig.java` before deploying to production.

---

## Profiles

| Profile   | When to use                      | DB         | ddl-auto     |
|-----------|----------------------------------|------------|--------------|
| (default) | Production-like with env vars    | PostgreSQL | validate     |
| `dev`     | Deployable dev server            | PostgreSQL | validate     |
| `local`   | Local dev with Docker Compose    | PostgreSQL | validate     |
| `prod`    | Production                       | PostgreSQL | validate     |

All profiles use Liquibase for schema management with `ddl-auto: validate`. The database schema is created and versioned through changelog files.

---

## API Reference

### Authentication

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "password"}'
# → 200 OK

# Login — returns a JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "password"}'
# → 200 { "token": "eyJhbGciOiJIUzM4NCJ9..." }
```

Register new users with `POST /api/auth/register`, then use the credentials to log in and obtain a JWT token.

### Samples (authenticated)

All endpoints require `Authorization: Bearer <token>` header.

```bash
# Create a sample
curl -X POST http://localhost:8080/api/samples \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{"name": "my first sample"}'
# → 201 { "id": "uuid", "name": "my first sample", "createdAt": "2026-01-01T00:00:00" }

# List all samples
curl http://localhost:8080/api/samples \
  -H 'Authorization: Bearer <token>'
# → 200 [ { "id": "uuid", "name": "my first sample", "createdAt": "..." } ]

# Get sample by ID
curl http://localhost:8080/api/samples/{id} \
  -H 'Authorization: Bearer <token>'
# → 200 { "id": "uuid", "name": "...", "createdAt": "..." }
# → 404 (not found)
```

---

## Project structure

```
src/main/java/com/example/
├── SpringTemplateApplication.java          Entry point
│
├── application/dto/
│   ├── request/                            Input DTOs (Java records)
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── SampleRequest.java
│   └── response/                           Output DTOs (Java records)
│       ├── LoginResponse.java
│       ├── SampleResponse.java
│       └── UserResponse.java
│
├── domain/
│   ├── exception/                          Domain exceptions
│   │   └── NotFoundException.java
│   ├── model/                              Domain models (plain Java)
│   │   ├── Sample.java
│   │   └── User.java
│   ├── port/
│   │   ├── inbound/                        Use case interfaces
│   │   │   ├── SampleUseCase.java
│   │   │   └── UserUseCase.java
│   │   └── outbound/                       Repository interfaces
│   │       ├── SampleRepository.java
│   │       └── UserRepository.java
│   └── service/                            Business logic
│       ├── SampleService.java
│       └── UserService.java
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/                         JPA entities
│   │   │   ├── SampleEntity.java
│   │   │   └── UserEntity.java
│   │   ├── repository/                     Spring Data JPA repos
│   │   │   ├── SampleJpaRepository.java
│   │   │   └── UserJpaRepository.java
│   │   └── adapter/                        Port → JPA adapter
│   │       ├── SampleRepositoryAdapter.java
│   │       └── UserRepositoryAdapter.java
│   ├── security/
│   │   ├── SecurityConfig.java             Spring Security configuration
│   │   ├── JwtTokenProvider.java           Token generation/validation
│   │   ├── JwtAuthenticationFilter.java    OncePerRequestFilter
│   │   └── CustomUserDetailsService.java   User lookup
│   └── web/
│       ├── SampleController.java           REST CRUD endpoints
│       ├── AuthController.java             Login/register endpoints
│       ├── ErrorResponse.java              Error DTO
│       └── GlobalExceptionHandler.java     @RestControllerAdvice
│
└── shared/
    ├── config/                             Shared configuration
    │   ├── CorsConfig.java
    │   └── JacksonConfig.java
    └── mapper/                             Domain ↔ DTO / Domain ↔ Entity
        ├── SampleMapper.java
        ├── SampleEntityMapper.java
        ├── UserMapper.java
        └── UserEntityMapper.java

src/main/resources/
├── application.yml                         Base config (PostgreSQL, JWT)
├── application-dev.yml                     Dev server overrides
├── application-local.yml                   Local development overrides
├── application-prod.yml                    Production overrides
└── db/changelog/
    └── db.changelog-master.yaml            Liquibase migrations

src/test/java/com/example/
├── BaseIntegrationTest.java                Shared container + auth helpers
├── TestContainersConfig.java               Singleton PostgreSQL container
├── domain/service/
│   ├── SampleServiceTest.java              Unit tests (Mockito)
│   └── UserServiceTest.java                Unit tests (Mockito)
└── infrastructure/web/
    ├── AuthIntegrationTest.java            Auth API tests (Testcontainers)
    └── SampleIntegrationTest.java          Sample CRUD API tests (Testcontainers)

config/
├── checkstyle/
│   └── checkstyle.xml                      Checkstyle rules
└── pmd/
    └── ruleset.xml                         PMD rules

infra/
└── docker-compose.yml                      PostgreSQL 17

scripts/
└── rename-package.sh                       Package renamer

.github/
└── workflows/
    └── ci.yml                              GitHub Actions CI

Dockerfile                                   Multi-stage build
```

---

## Configuration reference

| Property                   | Default                          | Description                         |
|----------------------------|----------------------------------|-------------------------------------|
| `DATABASE_URL`             | `jdbc:postgresql://localhost:5432/mydb` | JDBC connection URL       |
| `DATABASE_USERNAME`        | `postgres`                       | Database user                       |
| `DATABASE_PASSWORD`        | `postgres`                       | Database password                   |
| `JWT_SECRET`               | (256-bit placeholder)            | HMAC-SHA384 signing key             |
| `JWT_EXPIRATION`           | `86400000` (24h)                 | Token TTL in milliseconds           |
| `PORT`                     | `8080`                           | HTTP server port                    |
| `/actuator/health`         | —                                | Liveness probe (unauthenticated)    |

---

## Tests

All tests use **Testcontainers** with a real PostgreSQL container. Docker is required.

```bash
# Run all tests
./gradlew test

# Run with debug output
./gradlew test --info
```

```bash
# Run all code quality checks (formatting, style, static analysis)
./gradlew check
```

| Test class                          | What it verifies                              |
|-------------------------------------|-----------------------------------------------|
| `AuthIntegrationTest`               | Register, duplicate user, wrong password      |
| `SampleIntegrationTest`             | CRUD samples, 404 not found                   |
| `SampleServiceTest`                 | Unit tests for sample service logic           |
| `UserServiceTest`                   | Unit tests for user service logic             |

The Docker host is auto-detected via `docker context inspect` — works with Docker Desktop, colima, and GitHub Actions without manual configuration.

---

## CI

On every push and pull request to `main`, GitHub Actions runs:

- **Spotless** — code formatting check
- **Checkstyle** — style rules
- **PMD** — static analysis
- **All tests** with Testcontainers PostgreSQL (single shared container)

See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).

---

## Using as a template

1. **Rename the package** — `./scripts/rename-package.sh com.yourcompany` (or manually replace `com.example`)
2. **Rename the project** — update `settings.gradle.kts` (`rootProject.name`)
3. **Replace the Sample domain** — delete `Sample.java`, `SampleEntity.java`, `SampleService.java`, etc., and add your own
4. **Update Liquibase** — edit `db.changelog-master.yaml` with your tables
5. **Update Docker Compose** — change database name/password in `infra/docker-compose.yml` to match your `application.yml`
6. **Update the JWT secret** — generate a 256-bit key for production
7. **Enable CI** — push to GitHub, the workflow in `.github/workflows/ci.yml` runs automatically
8. **Set GitHub Secrets** — if deploying, add `JWT_SECRET`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` to your repository secrets

---

## Docker

```bash
# Start PostgreSQL
docker compose -f infra/docker-compose.yml up -d

# Build and run the application container
docker build -t spring-template .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  --network host \
  spring-template

# Stop and remove volumes
docker compose -f infra/docker-compose.yml down -v
```

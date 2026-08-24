# Smart Society Operations Platform

A production-oriented modular monolith for residential society operations: complaints, SLA tracking, notices, notifications, analytics, media, RBAC and auditability.

## Architecture
React + TypeScript frontend → Spring Boot REST API → PostgreSQL. Complaint photos are stored behind a media abstraction; local filesystem is the development provider and S3-compatible storage can be added without changing the domain layer.

## Quick start

### Backend
1. Start PostgreSQL (or use Docker Compose).
2. Copy `backend/.env.example` to `.env` and set secrets.
3. Run `./mvnw spring-boot:run` from `backend`.

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Docker
```bash
docker compose up --build
```

## Demo accounts
Seeded only when `APP_SEED_DEMO=true`:
- Admin: `admin@smartsociety.local` / `ChangeMe123!`
- Resident: `resident@smartsociety.local` / `ChangeMe123!`

Change these immediately for any public deployment.

## API
OpenAPI UI: `/swagger-ui.html` when backend is running.

## Engineering highlights
- Modular monolith; no unnecessary distributed infrastructure.
- JWT access tokens with role checks and backend ownership enforcement.
- Complaint state machine with immutable transition history.
- Configurable SLA by category and priority.
- Database-side dashboard aggregation and paginated complaint queries.
- Transactional domain changes with asynchronous notification dispatch.
- Media abstraction prevents binary images from entering PostgreSQL.
- Flyway migrations, integration-test structure, Actuator health checks and GitHub Actions.

See `docs/architecture.md`, `docs/database-design.md`, `docs/api-design.md`, `docs/security.md`, and `docs/system-design.md`.

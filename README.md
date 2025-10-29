# Backend – Spring Boot (Events API)

CRUD APIs for calendar events with PostgreSQL storage and Flyway migrations.

## Stack
- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA (Hibernate)
- Flyway (schema migrations) + `flyway-database-postgresql`
- PostgreSQL 16 (local)

## Setup
1) Ensure PostgreSQL is running locally (5432 by default).
2) Create a role and database that match the config below (adjust port if needed):
```bash
psql -h localhost -p 5432 -d postgres -U "$USER" -c "DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'postgres') THEN
      CREATE ROLE postgres WITH LOGIN SUPERUSER PASSWORD 'postgres';
   END IF;
END$$;"
psql -h localhost -p 5432 -d postgres -U postgres -c "CREATE DATABASE calendar OWNER postgres;"
```

## Configuration
`src/main/resources/application.properties` (defaults shown):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/calendar
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```
You can override via env vars when running:
```bash
DB_URL=jdbc:postgresql://localhost:5432/calendar \
DB_USERNAME=postgres \
DB_PASSWORD=postgres \
./mvnw spring-boot:run
```

## Run
```bash
cd backend
./mvnw spring-boot:run
```
- Flyway runs on startup and ensures the `events` table exists (`db/migration/V1__create_events.sql`).
- API base URL: `http://localhost:8080`

## API
- GET `/api/events`
  - Optional query: `start` and `end` (epoch millis). Returns events with `day` within range.
- GET `/api/events/{id}`
- POST `/api/events`
- PUT `/api/events/{id}`
- DELETE `/api/events/{id}`

Event JSON:
```json
{
  "id": "1700000000000",
  "title": "Meeting",
  "description": "Team sync",
  "label": "blue",
  "day": 1730160000000
}
```
Notes:
- `id` is client-generated (string). Current UI uses `Date.now()`.
- `day` is the selected date as epoch millis (local-day granularity).

## Architecture
- Layering: Controller → Service → Repository (JPA).
- JPA entity: `Event` maps to `events` table (PK: `id`).
- Flyway manages schema: versioned SQL in `src/main/resources/db/migration`.
- CORS: allowed origin `http://localhost:3000` (dev UI).
- Dependencies: see `pom.xml` (includes `flyway-database-postgresql` for PG 16 support).

## Business logic & edge cases
- Validation: `title` (required), `day` (required). 400 on invalid payload.
- Overlaps: allowed; backend doesn’t prevent multiple events on the same day.
- Recurring events: not implemented; planned as RRULE storage and server-side expansion.
- Ranges: `GET /api/events?start&end` filters by `day` inclusive.
- Timezones: stores `day` as epoch millis; for TZ-robust logic, switch to `start`/`end` instants.
- IDs: upsert semantics on PUT for an existing `id`; POST requires `id`.
- Errors: 404 for missing resource; 204 on successful delete; error text on non-2xx.

## Schema
`V1__create_events.sql`:
```sql
CREATE TABLE IF NOT EXISTS events (
  id VARCHAR(64) PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  label VARCHAR(32),
  day BIGINT NOT NULL
);
```

## Troubleshooting
- FATAL: role "postgres" does not exist
  - Create the role or change `spring.datasource.username/password` to a user that exists.
- Unsupported Database: PostgreSQL 16.x (Flyway)
  - Ensure `flyway-database-postgresql` is present in `pom.xml` (already added).
- Connection refused / port in use
  - Verify the port (`5432` vs `5433`) and update `spring.datasource.url` accordingly.
- Missing migration locally but recorded in DB
  - Restore the SQL file or (temporary) set `spring.flyway.ignore-missing-migrations=true`.

## Future work (backend)
- Start/end timestamps and multi-day spanning events.
- Pagination and sorting for list endpoints.
- Indices on frequently queried columns (e.g., `day`).
- Soft deletes, auditing (created_at/updated_at, user context).
- Auth and per-user data isolation (multi-tenant or user_id scoping).

# Backend – Spring Boot (Events API)

CRUD APIs for calendar events. PostgreSQL for storage. Flyway for schema migrations.

## Requirements
- Java 17+
- PostgreSQL 16 (local)

## Config
`src/main/resources/application.properties` (defaults shown)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/calendar
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
```
Change the URL/credentials if your Postgres runs on a different port/user.

## Database setup
Create a role and database that match your config.
```bash
# Create superuser role (only if it doesn't exist)
psql -h localhost -p 5432 -d postgres -U "$USER" -c "DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'postgres') THEN
      CREATE ROLE postgres WITH LOGIN SUPERUSER PASSWORD 'postgres';
   END IF;
END$$;"

# Create database owned by that role
psql -h localhost -p 5432 -d postgres -U postgres -c "CREATE DATABASE calendar OWNER postgres;"
```
If you use another port (e.g., 5433), update the JDBC URL accordingly.

## Run
```bash
cd backend
./mvnw spring-boot:run
```
Flyway runs on startup and ensures the `events` table exists.

## API
Base: `http://localhost:8080`
- GET `/api/events` (optional `?start=epochMillis&end=epochMillis`)
- GET `/api/events/{id}`
- POST `/api/events`
- PUT `/api/events/{id}`
- DELETE `/api/events/{id}`

Event JSON
```json
{
  "id": "1700000000000",
  "title": "Meeting",
  "description": "Team sync",
  "label": "blue",
  "day": 1730160000000
}
```

## Notes
- Data persists across app restarts; migrations manage schema only.
- CORS allows `http://localhost:3000` by default.
- If you see “role \"postgres\" does not exist”, create it or set `spring.datasource.username/password` to a user that exists.

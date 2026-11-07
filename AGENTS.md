# Repository Guidelines

## Project Structure & Module Organization

This repository contains a Spring Boot 3 backend in `backend/` and a React/Vite frontend in `frontend/`. Backend Java code lives under `backend/src/main/java/mx/taller`, grouped by feature (`auth`, `client`, `vehicle`, `workshop`) with shared code in `common` and `security`. Configuration is in `backend/src/main/resources/`. Frontend entry points and components are in `frontend/src/`; built assets go to `frontend/dist/`. The MySQL schema is `database.sql`, migrations are in `database/migrations/`, and Mermaid diagrams are in `diagramas/`.

## Build, Test, and Development Commands

- `cd backend && mvn spring-boot:run` starts the API; configure `DB_URL`, `DB_USER`, and `DB_PASSWORD` first.
- `cd backend && mvn test` runs backend tests; `mvn package` builds the application artifact.
- `cd frontend && pnpm install` installs dependencies from the pnpm lockfile.
- `cd frontend && pnpm run start` starts Vite; `pnpm run build` creates production assets in `dist/`.
- Configure `VITE_API_URL` when the API is not at `http://localhost:8080/api`. For local database initialization, apply `database.sql` to MySQL 8.

## Coding Style & Naming Conventions

Follow the Java package-by-feature structure and standard naming: `PascalCase` classes, `camelCase` methods and fields, with descriptive Spanish domain names. Keep controllers, DTOs, repositories, and entities in their feature packages. In React, use `.jsx` for components and `camelCase` for functions and variables; keep shared API calls in `frontend/src/services/`. Match the surrounding file’s formatting, use clear small changes, and avoid adding dependencies without need. No formatter or linter is configured in the current frontend scripts.

## Testing Guidelines

There are no test files or frontend test scripts currently defined. Add backend tests under `backend/src/test/java` and run them with `mvn test`. If frontend tests are introduced, add an explicit package script and document its command here. For API changes, check the affected endpoint and consuming UI flow.

## Commit & Pull Request Guidelines

Git history is unavailable in this checkout, so no repository-specific commit convention can be confirmed. Use short, imperative commit subjects that identify the area and change, such as `vehicle: validate uploaded photo`. Pull requests should explain the change, list affected areas, note setup or migration steps, link related issues, include UI screenshots when relevant, and mention verification commands.

## Security & Configuration Tips

Keep credentials and local environment values out of commits. Use the environment variables documented in `README.md`; never store plaintext passwords or expose uploaded images through unauthenticated routes. Include a migration when changing the database schema and describe any required initialization steps.

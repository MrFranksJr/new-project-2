# Ways of Working: AGENTS.md

Welcome to the Gaming Tracker development guide for AI agents. This project follows strict Extreme Programming (XP) and Clean Architecture principles.

## Core Values

1. **Test-Driven Development (TDD) First**
   - No production code is written without a failing test first.
   - Tests serve as the primary documentation of intent and domain language.
   - Aim for 100% logic coverage in the domain and application layers.

2. **Clean Architecture (Hexagonal)**
   - **Domain:** Pure Kotlin logic, no dependencies on frameworks or libraries.
   - **Application (Ports/Use Cases):** Defines the "what" of the system. Depends only on the Domain.
   - **Infrastructure (Adapters):** Implements the "how" (SQLite, JNA, Ktor, Compose). Dependencies flow inward.

3. **Incremental Delivery**
   - Break tasks into small, verifiable steps.
   - Maintain a working build at all times.

4. **Domain-Driven Design (DDD)**
   - Use clear, ubiquitous language (e.g., `GamingSession`, `TrackGameSession`, `GamingPC`).
   - Keep models focused and split them into separate files as they grow.

## Technical Standards

- **Kotlin:** Use modern idiomatic Kotlin features. Follow the project's existing code style.
- **Dependency Inversion:** Use interfaces (Ports) for all infrastructure interactions (Persistence, OS-level calls).
- **Automation:** Every change should be verifiable via `./gradlew test`.

## AI Interaction Guidelines

- **Analyze Before Acting:** Always understand the existing structure and requirements before making changes.
- **Update the Masterplan:** Keep `MASTERPLAN.md` at the root updated with progress.
- **Self-Correction:** If a test fails, treat it as a priority and fix it before moving on.

# Gaming Tracker: Masterplan

## Phase 1: Infrastructure & Project Setup ✓
- [x] 1.1. Backend Module Setup (Compose, Ktor, Gradle)
- [x] 1.2. Frontend Module Setup (Vite, React, TypeScript)
- [x] 1.3. TDD Setup (JUnit, MockK, Vitest)
- [x] 1.4. Hexagonal Architecture Implementation (Initial structure)

## Phase 2: Core Domain & TDD (Kotlin) ✓
- [x] 2.1. Define Domain Entities and Application Ports
- [x] 2.2. Implement SQLite Persistence Layer (Exposed)
    - [x] 2.2.1. Create Exposed Schema (Tables)
    - [x] 2.2.2. Implement Repository Adapters (Game, Session, GamingPC)
    - [x] 2.2.3. Write Integration Tests for Repositories
- [x] 2.3. Implement Legacy Migration Service (Import from legacy GamingGaiden.db)
- [x] 2.4. Implement Windows System Integration (JNA for process monitoring)
- [x] 2.5. Integrate HWiNFO64 Registry Updates
- [x] 2.6. Create Backend Entry Point (Main.kt) and App Wiring
- [x] 2.7. Implement Add Game Logic

## Phase 3: Modernized UI (React/TypeScript) ✓
- [x] 3.1. Design Modern Component Architecture
- [x] 3.2. Implement Summary and Game List Views
- [x] 3.3. Establish API Communication (Ktor + Zustand/TanStack Query)
- [x] 3.4. Embed UI in Compose for Desktop (WebView/JCEF)
- [x] 3.5. Implement Add Game UI and Update Notifications

## Phase 4: Packaging & Distribution ✓
- [x] 4.1. Configure jpackage / Compose Gradle Plugin for MSI/EXE
- [x] 4.2. Implement Auto-Update Check Logic

## Phase 5: Testing & Verification ✓
- [x] 5.1. 100% Coverage for Core Tracking Logic (Unit Tests)
- [x] 5.2. Integration Tests for Persistence and Migrations
- [x] 5.3. E2E Validation of Full User Journey

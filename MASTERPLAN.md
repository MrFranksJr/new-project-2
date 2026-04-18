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

## Phase 6: Refactoring & Test Completion ✓
- [x] 6.1. Rename 'execute' methods to domain-specific verbs in use cases
- [x] 6.2. Add Kover for backend coverage analysis
- [x] 6.3. Set up Vitest and React Testing Library for frontend
- [x] 6.4. Implement frontend unit and component tests
- [x] 6.5. Improve backend coverage for repositories and legacy adapters
- [x] 6.6. Remove unused repository and service methods (Cleanup)

## Phase 7: Tray and Autostart Features ✓
- [x] 7.1. Backend autostart with TDD (ports/usecases/tests/impl/JNA/endpoints)
- [x] 7.2. System tray with menu (Open Summary/Games/Add/Exit), window hide-on-close
- [x] 7.3. Frontend toggle switch UI (query/mutation)

## Phase 8: Uninstall Cleanup Flow
- [x] 8.1. Verify MSI/EXE uninstall default behavior (installer artifacts removed; app data/registry custom cleanup handled in-app)
- [x] 8.2. Backend cleanup flow with TDD (CleanupPort/PerformCleanup, WindowsCleanupManager, `/api/cleanup`)
- [x] 8.3. Uninstall UX entry points (tray `Uninstall` + frontend cleanup view with optional DB deletion)

## Phase 9: Maintenance
- [x] 9.1. Upgrade Kotlin Gradle plugins from 2.0.0 to 2.3.20
- [x] 9.2. Remediate frontend dependency vulnerabilities (Vite advisory range) and verify with `npm audit` + `./gradlew test`
- [x] 9.3. Remediate backend dependency vulnerabilities via safe version upgrades (Ktor 3.4.2/JNA/Logback/test libs/Exposed 1.2.0) and verify with `./gradlew test` (Fixed H2 2.4+ compatibility and Netty CVE-2026-33870 by forcing 4.2.12.Final)

## Phase 10: Technical Debt & Modernization ✓
- [x] 10.1. Replace deprecated Exposed DSL methods in repositories and tests (0.50.1 migration)
- [x] 10.2. Migrate frontend build to Gradle Node plugin for environment portability
- [x] 10.3. Synchronize IDE Kotlin compiler version with Gradle (2.3.20)
- [x] 10.4. Cleanup temporary diagnostic files (compile_errors.txt, deps_compile.txt)
- [x] 10.5. Fix SQLite JDBC driver class name and prevent legacy database from overriding default connection
- [x] 10.6. Fix Skiko linkage error by aligning Compose Gradle plugin (1.7.1) with transitive Compose version
- [x] 10.7. Resolve white window bug by using root-based Vite asset paths and fixing filename mismatches
- [x] 10.8. Address Java 25 / Netty native access warnings with JVM arguments
- [x] 10.9. Add Ktor CallLogging for improved frontend-to-backend diagnostics
- [x] 10.10. Implement server-readiness check before UI launch to prevent initial load failure in WebView
- [x] 10.11. Update build inputs to ensure robust frontend-to-backend resource synchronization
- [x] 10.12. Disable Vite filename hashing for stable asset references
- [x] 10.13. Add diagnostic background color to index.html for UI troubleshooting
- [x] 10.14. Verify Ktor MIME types for JS/CSS assets via automated tests
- [x] 10.15. Add classpath diagnostics and WebView status bar for deep troubleshooting
- [x] 10.16. Implement explicit routing with diagnostics for static index.html

## Phase 11: Pivot to Browser-as-UI Architecture
- [ ] 11.1. Strip KCEF and WebView dependencies from backend
- [ ] 11.2. Implement "Browser-as-UI" mechanism (launch system browser from tray)
- [ ] 11.3. Remove Compose Window shell and manage lifecycle via Tray
- [ ] 11.4. Verify backend background service stability

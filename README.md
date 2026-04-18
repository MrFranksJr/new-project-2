# Gaming Gaiden Rebirth

**Gaming Gaiden Rebirth** is a modern, cross-language gaming session tracker designed specifically for Windows users. Evolved from its legacy Delphi/C# roots, this project leverages a clean, hexagonal architecture and modern technologies to provide an unobtrusive and feature-rich gaming dashboard.

---

### 🎮 Features

- **Automated Background Tracking**: Using JNA and Windows Win32 APIs, the application silently monitors running processes and automatically matches them against your game library to record sessions and playtime.
- **Hardware Telemetry Integration**: Real-time integration with **HWiNFO64** (via Windows Registry) to display system stats like CPU/GPU temperatures and power usage during your gaming sessions.
- **Legacy Database Migration**: Seamlessly import your historical gaming data from the original `GamingGaiden.db` into the new modern architecture.
- **Modern Unified UI**: A sleek React-based frontend built with Vite and TypeScript, seamlessly embedded within a native Compose for Desktop shell using a high-performance WebView.
- **Auto-Update Check**: Intelligent semver-based logic that alerts you when a newer version of the tracker is available for download.

---

### 🛠️ Technical Stack

- **Backend (Kotlin 2.1 / JVM)**:
    - **Clean Architecture**: Domain, Application (Use Cases), and Infrastructure layers.
    - **Ktor 3.0**: Embedded server for the local API and static file serving.
    - **Exposed ORM**: High-level database interaction for the SQLite persistence layer.
    - **JNA (Java Native Access)**: Direct communication with the Windows kernel for process monitoring.
- **Frontend (React / TypeScript)**:
    - **Vite**: Ultra-fast build tool and dev server.
    - **Zustand / TanStack Query**: Modern state management and data fetching.
- **UI Shell (Compose for Desktop)**:
    - Provides a native Windows window and integrates the React frontend via a WebView component.

---

### 🚀 Development Setup

#### Prerequisites
- **JDK 17 or higher**
- **Node.js (v18+) and npm**
- **Windows OS** (Required for process tracking and hardware telemetry features)

#### Running Locally
1. **Clone the repository.**
2. **Build the Frontend**:
   ```bash
   cd frontend
   npm install
   npm run build
   ```
3. **Run the Backend**:
   From the project root:
   ```bash
   ./gradlew run
   ```
   *Note: This will launch the Compose window, which will serve the built frontend from `frontend/dist` via the internal Ktor server.*

---

### 📦 Deployment & Packaging

The application is configured to be packaged as a native Windows installer (`.exe` or `.msi`) using the Compose Gradle plugin and `jpackage`.

#### To Create a Portable Executable:
```bash
./gradlew :backend:packageExe
```

#### To Create a Windows Installer (MSI):
```bash
./gradlew :backend:packageMsi
```

The output artifacts will be located in:
`backend/build/compose/binaries/main/`

*Important: Packaging must be performed on a Windows machine to generate the `.exe` or `.msi` formats.*

---

### 🔄 Update Mechanism

The application includes a built-in update check logic:
- The backend periodically checks a version endpoint (configured in `CheckForUpdates.kt`).
- It uses semantic versioning (SemVer) to compare the local version (defined in `build.gradle.kts`) with the remote version.
- If a newer version is found, an update notification banner is displayed in the UI.

---

### 📂 Data Management

- **Database**: All data is stored in a local SQLite database file named `gaming-tracker.db` in the application's working directory.
- **Legacy Migration**: To import data from your old tracker, place your `GamingGaiden.db` file in the same directory as the executable and use the migration trigger in the UI or via the `/api/migrate` endpoint.
- **Hardware Stats**: Ensure **HWiNFO64** is running and its "Shared Memory" support is enabled (required for the Windows Registry updates to be readable).

---

### 🏗️ Ways of Working

For details on the project's development principles (TDD, DDD, Clean Architecture), please refer to [AGENTS.md](./AGENTS.md).

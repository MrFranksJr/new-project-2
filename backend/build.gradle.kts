plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}

group = "com.gamingtracker"
version = "1.0-SNAPSHOT"

val ktorVersion = "3.4.2"

dependencies {
    // Compose for Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)

    // Exposed ORM for SQLite
    val exposedVersion = "0.50.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")

    // Ktor for Local API
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // JNA for Process Detection
    implementation("net.java.dev.jna:jna:5.17.0")
    implementation("net.java.dev.jna:jna-platform:5.17.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.20")

    // WebView for Compose
    implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.ktor") {
            useVersion(ktorVersion)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

val buildFrontend = tasks.register<Exec>("buildFrontend") {
    group = "build"
    workingDir = File(project.rootDir, "frontend")
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "npm install && npm run build")
    } else {
        commandLine("bash", "-c", "npm install && npm run build")
    }
}

// Ensure frontend is built before resources are processed
tasks.processResources {
    dependsOn(buildFrontend)
    from(File(project.rootDir, "frontend/dist")) {
        into("static")
    }
}

compose.desktop {
    application {
        mainClass = "com.gamingtracker.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "GamingTracker"
            packageVersion = "1.0.0"
            description = "Gaming Tracker - Modernized Gaming Session Tracker"
            copyright = "© 2026 Gaming Tracker"
            vendor = "Gaming Tracker"
            
            windows {
                menuGroup = "Games"
                shortcut = true
                dirChooser = true
                upgradeUuid = "d7d1e8c0-8a4b-4b2a-8c9e-6b7d8c9d0e1f"
            }
        }
    }
}

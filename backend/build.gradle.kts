plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.gaminggaiden"
version = "1.0-SNAPSHOT"

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
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")

    // Ktor for Local API
    val ktorVersion = "2.3.11"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // JNA for Process Detection
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // WebView for Compose
    implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.40")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("org.assertj:assertj-core:3.26.0")
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "com.gaminggaiden.rebirth.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "GamingGaidenRebirth"
            packageVersion = "1.0.0"
        }
    }
}

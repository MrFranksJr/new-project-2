package com.gamingtracker.infrastructure.api.ktor

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorStaticTest {

    @Test
    fun `should serve index html and assets`() = testApplication {
        application {
            configureRouting(
                getGamesUseCase = mockk(),
                getSummaryUseCase = mockk(),
                migrateLegacyDataUseCase = mockk(),
                addGameUseCase = mockk(),
                getUpdateStatusUseCase = mockk(),
                toggleAutostartUseCase = mockk(),
                getAutostartStatusUseCase = mockk(),
                performCleanupUseCase = mockk()
            )
        }

        // Test root serves index.html
        client.get("/").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("<!doctype html>"))
            assertTrue(response.bodyAsText().contains("root"))
        }

        // Test an asset (we know one exists from our previous ls)
        client.get("/assets/index-BOw7KOIo.js").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("window"), "JS bundle should contain some common JS keywords")
        }
    }
}

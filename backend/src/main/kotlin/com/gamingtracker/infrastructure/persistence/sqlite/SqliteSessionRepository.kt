package com.gamingtracker.infrastructure.persistence.sqlite

import com.gamingtracker.application.ports.output.SessionRepository
import com.gamingtracker.domain.GamingSession
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SqliteSessionRepository : SessionRepository {
    override fun save(session: GamingSession) = transaction {
        GamingSessionsTable.insert {
            it[gameName] = session.gameName
            it[startTime] = session.startTime
            it[durationMinutes] = session.durationMinutes
        }
        Unit
    }
}

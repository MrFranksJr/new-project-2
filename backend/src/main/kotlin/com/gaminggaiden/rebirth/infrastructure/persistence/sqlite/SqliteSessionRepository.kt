package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.application.ports.output.SessionRepository
import com.gaminggaiden.rebirth.domain.GamingSession
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

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

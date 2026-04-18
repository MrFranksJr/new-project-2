package com.gamingtracker.infrastructure.persistence.sqlite

import com.gamingtracker.application.ports.output.LegacyDatabasePort
import com.gamingtracker.domain.Game
import com.gamingtracker.domain.GameStatus
import com.gamingtracker.domain.GamingSession
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SqliteLegacyDatabaseAdapter(private val database: Database) : LegacyDatabasePort {

    constructor(dbPath: String) : this(Database.connect("jdbc:sqlite:$dbPath", driver = "org.xerial.sqlite.JDBC"))

    object LegacyGamesTable : Table("games") {
        val name = varchar("name", 255)
        val exeName = varchar("exe_name", 255)
        val playtimeMinutes = long("playtime_minutes").default(0)
        val sessionCount = integer("session_count").default(0)
        val lastPlayDate = timestamp("last_play_date").nullable()
        val status = varchar("status", 50).default("UNPLAYED")
        
        override val primaryKey = PrimaryKey(name)
    }

    object LegacySessionsTable : Table("sessions") {
        val id = integer("id").autoIncrement()
        val gameName = varchar("game_name", 255)
        val startTime = timestamp("start_time")
        val durationMinutes = long("duration_minutes")
        
        override val primaryKey = PrimaryKey(id)
    }

    override fun fetchAllGames(): List<Game> = transaction(database) {
        LegacyGamesTable.selectAll().map {
            Game(
                name = it[LegacyGamesTable.name],
                exeName = it[LegacyGamesTable.exeName],
                playtimeMinutes = it[LegacyGamesTable.playtimeMinutes],
                sessionCount = it[LegacyGamesTable.sessionCount],
                lastPlayDate = it[LegacyGamesTable.lastPlayDate],
                status = try { GameStatus.valueOf(it[LegacyGamesTable.status]) } catch (e: Exception) { GameStatus.UNPLAYED },
                gamingPcs = emptyList()
            )
        }
    }

    override fun fetchAllSessions(): List<GamingSession> = transaction(database) {
        LegacySessionsTable.selectAll().map {
            GamingSession(
                gameName = it[LegacySessionsTable.gameName],
                startTime = it[LegacySessionsTable.startTime],
                durationMinutes = it[LegacySessionsTable.durationMinutes]
            )
        }
    }
}

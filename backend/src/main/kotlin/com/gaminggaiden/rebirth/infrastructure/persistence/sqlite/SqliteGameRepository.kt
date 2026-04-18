package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.application.ports.output.GameRepository
import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GameStatus
import com.gaminggaiden.rebirth.domain.GamingPC
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SqliteGameRepository : GameRepository {
    override fun findByName(name: String): Game? = transaction {
        GamesTable.select { GamesTable.name eq name }
            .map { toDomain(it) }
            .singleOrNull()
    }

    override fun findByExeName(exeName: String): Game? = transaction {
        GamesTable.select { GamesTable.exeName eq exeName }
            .map { toDomain(it) }
            .singleOrNull()
    }

    override fun save(game: Game) = transaction {
        val exists = GamesTable.select { GamesTable.name eq game.name }.any()
        if (exists) {
            GamesTable.update({ GamesTable.name eq game.name }) {
                it[exeName] = game.exeName
                it[playtimeMinutes] = game.playtimeMinutes
                it[sessionCount] = game.sessionCount
                it[lastPlayDate] = game.lastPlayDate
                it[status] = game.status.name
            }
        } else {
            GamesTable.insert {
                it[name] = game.name
                it[exeName] = game.exeName
                it[playtimeMinutes] = game.playtimeMinutes
                it[sessionCount] = game.sessionCount
                it[lastPlayDate] = game.lastPlayDate
                it[status] = game.status.name
            }
        }

        // Sync PCs
        GameGamingPCsTable.deleteWhere { gameName eq game.name }
        game.gamingPcs.forEach { pc ->
            // Ensure PC exists
            if (!GamingPCsTable.select { GamingPCsTable.name eq pc.name }.any()) {
                GamingPCsTable.insert {
                    it[name] = pc.name
                    it[totalPlaytimeMinutes] = pc.totalPlaytimeMinutes
                    it[inUse] = pc.inUse
                }
            }
            GameGamingPCsTable.insert {
                it[gameName] = game.name
                it[pcName] = pc.name
            }
        }
    }

    override fun getAllGames(): List<Game> = transaction {
        GamesTable.selectAll().map { toDomain(it) }
    }

    private fun toDomain(row: ResultRow): Game {
        val name = row[GamesTable.name]
        val pcs = GameGamingPCsTable
            .join(GamingPCsTable, JoinType.INNER, additionalConstraint = { GameGamingPCsTable.pcName eq GamingPCsTable.name })
            .select { GameGamingPCsTable.gameName eq name }
            .map {
                GamingPC(
                    name = it[GamingPCsTable.name],
                    totalPlaytimeMinutes = it[GamingPCsTable.totalPlaytimeMinutes],
                    inUse = it[GamingPCsTable.inUse]
                )
            }

        return Game(
            name = name,
            exeName = row[GamesTable.exeName],
            playtimeMinutes = row[GamesTable.playtimeMinutes],
            sessionCount = row[GamesTable.sessionCount],
            lastPlayDate = row[GamesTable.lastPlayDate],
            status = GameStatus.valueOf(row[GamesTable.status]),
            gamingPcs = pcs
        )
    }
}

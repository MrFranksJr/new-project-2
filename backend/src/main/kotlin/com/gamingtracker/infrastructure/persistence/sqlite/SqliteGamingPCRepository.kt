package com.gamingtracker.infrastructure.persistence.sqlite

import com.gamingtracker.application.ports.output.GamingPCRepository
import com.gamingtracker.domain.GamingPC
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class SqliteGamingPCRepository : GamingPCRepository {
    override fun findInUse(): GamingPC? = transaction {
        GamingPCsTable.selectAll().where { GamingPCsTable.inUse eq true }
            .map { toDomain(it) }
            .singleOrNull()
    }

    override fun save(pc: GamingPC): Unit = transaction {
        val exists = GamingPCsTable.selectAll().where { GamingPCsTable.name eq pc.name }.any()
        if (exists) {
            GamingPCsTable.update({ GamingPCsTable.name eq pc.name }) {
                it[totalPlaytimeMinutes] = pc.totalPlaytimeMinutes
                it[inUse] = pc.inUse
            }
        } else {
            GamingPCsTable.insert {
                it[name] = pc.name
                it[totalPlaytimeMinutes] = pc.totalPlaytimeMinutes
                it[inUse] = pc.inUse
            }
        }

        // If this PC is set to inUse, ensure all other PCs are set to not inUse
        if (pc.inUse) {
            GamingPCsTable.update({ GamingPCsTable.name neq pc.name }) {
                it[inUse] = false
            }
        }
    }

    private fun toDomain(row: ResultRow): GamingPC {
        return GamingPC(
            name = row[GamingPCsTable.name],
            totalPlaytimeMinutes = row[GamingPCsTable.totalPlaytimeMinutes],
            inUse = row[GamingPCsTable.inUse]
        )
    }
}

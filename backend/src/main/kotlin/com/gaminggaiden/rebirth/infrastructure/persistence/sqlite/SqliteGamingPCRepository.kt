package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import com.gaminggaiden.rebirth.application.ports.output.GamingPCRepository
import com.gaminggaiden.rebirth.domain.GamingPC
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class SqliteGamingPCRepository : GamingPCRepository {
    override fun findByName(name: String): GamingPC? = transaction {
        GamingPCsTable.select { GamingPCsTable.name eq name }
            .map { toDomain(it) }
            .singleOrNull()
    }

    override fun findInUse(): GamingPC? = transaction {
        GamingPCsTable.select { GamingPCsTable.inUse eq true }
            .map { toDomain(it) }
            .singleOrNull()
    }

    override fun save(pc: GamingPC): Unit = transaction {
        val exists = GamingPCsTable.select { GamingPCsTable.name eq pc.name }.any()
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
        Unit
    }

    override fun getAll(): List<GamingPC> = transaction {
        GamingPCsTable.selectAll().map { toDomain(it) }
    }

    private fun toDomain(row: ResultRow): GamingPC {
        return GamingPC(
            name = row[GamingPCsTable.name],
            totalPlaytimeMinutes = row[GamingPCsTable.totalPlaytimeMinutes],
            inUse = row[GamingPCsTable.inUse]
        )
    }
}

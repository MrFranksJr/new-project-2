package com.gaminggaiden.rebirth.infrastructure.persistence.sqlite

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object GamesTable : Table("games") {
    val name = varchar("name", 255)
    val exeName = varchar("exe_name", 255)
    val playtimeMinutes = long("playtime_minutes").default(0)
    val sessionCount = integer("session_count").default(0)
    val lastPlayDate = timestamp("last_play_date").nullable()
    val status = varchar("status", 50)
    
    override val primaryKey = PrimaryKey(name)
}

object GamingSessionsTable : Table("gaming_sessions") {
    val id = long("id").autoIncrement()
    val gameName = varchar("game_name", 255) references GamesTable.name
    val startTime = timestamp("start_time")
    val durationMinutes = long("duration_minutes")
    
    override val primaryKey = PrimaryKey(id)
}

object GamingPCsTable : Table("gaming_pcs") {
    val name = varchar("name", 255)
    val totalPlaytimeMinutes = long("total_playtime_minutes").default(0)
    val inUse = bool("in_use").default(false)
    
    override val primaryKey = PrimaryKey(name)
}

object GameGamingPCsTable : Table("game_gaming_pcs") {
    val gameName = varchar("game_name", 255) references GamesTable.name
    val pcName = varchar("pc_name", 255) references GamingPCsTable.name
    
    override val primaryKey = PrimaryKey(gameName, pcName)
}

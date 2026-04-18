package com.gamingtracker.application.ports.output

import com.gamingtracker.domain.Game
import com.gamingtracker.domain.GamingPC
import com.gamingtracker.domain.GamingSession

interface GameRepository {
    fun findByName(name: String): Game?
    fun findByExeName(exeName: String): Game?
    fun save(game: Game)
    fun getAllGames(): List<Game>
}

interface SessionRepository {
    fun save(session: GamingSession)
}

interface GamingPCRepository {
    fun findInUse(): GamingPC?
    fun save(pc: GamingPC)
}

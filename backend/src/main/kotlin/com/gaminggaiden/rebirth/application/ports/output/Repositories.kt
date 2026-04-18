package com.gaminggaiden.rebirth.application.ports.output

import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GamingPC
import com.gaminggaiden.rebirth.domain.GamingSession

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

package com.gaminggaiden.rebirth.application.ports.output

import com.gaminggaiden.rebirth.domain.Game
import com.gaminggaiden.rebirth.domain.GamingSession

interface LegacyDatabasePort {
    fun fetchAllGames(): List<Game>
    fun fetchAllSessions(): List<GamingSession>
}

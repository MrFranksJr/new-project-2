package com.gamingtracker.application.ports.output

import com.gamingtracker.domain.Game
import com.gamingtracker.domain.GamingSession

interface LegacyDatabasePort {
    fun fetchAllGames(): List<Game>
    fun fetchAllSessions(): List<GamingSession>
}

import { describe, it, expect, vi } from 'vitest'
import axios from 'axios'

vi.mock('axios', () => {
  const mockApi = {
    get: vi.fn(),
    post: vi.fn(),
  }
  return {
    default: {
      create: vi.fn(() => mockApi),
    },
  }
})

// Import after mocking
import { gameService } from './api'

describe('api service', () => {
  it('getGames should fetch games', async () => {
    const games = [{ name: 'Hades', exeName: 'Hades.exe', playtimeMinutes: 0, sessionCount: 0, status: 'UNPLAYED', lastPlayDate: null }]
    vi.mocked(axios.create().get).mockResolvedValueOnce({ data: { games } })

    const result = await gameService.getGames()
    expect(result).toEqual(games)
  })

  it('getSummary should fetch summary', async () => {
    const summary = { totalPlaytimeMinutes: 100, activeGameName: 'Hades', gamingPCName: 'MyRig', systemStats: {} }
    vi.mocked(axios.create().get).mockResolvedValueOnce({ data: summary })

    const result = await gameService.getSummary()
    expect(result).toEqual(summary)
  })

  it('addGame should post new game', async () => {
    const game = { name: 'Starfield', exeName: 'Starfield.exe' }
    vi.mocked(axios.create().post).mockResolvedValueOnce({ data: { ...game, playtimeMinutes: 0, sessionCount: 0, status: 'UNPLAYED', lastPlayDate: null } })

    const result = await gameService.addGame('Starfield', 'Starfield.exe')
    expect(result.name).toBe('Starfield')
  })
})

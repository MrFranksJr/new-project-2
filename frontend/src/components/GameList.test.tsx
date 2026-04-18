import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import GameList from './GameList'
import type { Game } from '../types'

describe('GameList', () => {
  it('should show loading state', () => {
    render(<GameList games={undefined} isLoading={true} />)
    expect(screen.getByText(/Loading games/i)).toBeInTheDocument()
  })

  it('should show empty state', () => {
    render(<GameList games={[]} isLoading={false} />)
    expect(screen.getByText(/No games tracked yet/i)).toBeInTheDocument()
  })

  it('should render a list of games', () => {
    const games: Game[] = [
      { name: 'Game 1', exeName: 'game1.exe', playtimeMinutes: 10, sessionCount: 1, status: 'PLAYING', lastPlayDate: null },
      { name: 'Game 2', exeName: 'game2.exe', playtimeMinutes: 20, sessionCount: 2, status: 'COMPLETED', lastPlayDate: null }
    ]
    render(<GameList games={games} isLoading={false} />)
    
    expect(screen.getByText('Game 1')).toBeInTheDocument()
    expect(screen.getByText('Game 2')).toBeInTheDocument()
  })
})

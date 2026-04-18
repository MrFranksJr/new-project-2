import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import GameItem from './GameItem'
import type { Game } from '../types'

describe('GameItem', () => {
  it('should render game details', () => {
    const game: Game = {
      name: 'Hades',
      exeName: 'Hades.exe',
      playtimeMinutes: 45,
      sessionCount: 3,
      status: 'PLAYING',
      lastPlayDate: '2026-04-18T10:00:00Z'
    }
    render(<GameItem game={game} />)
    
    expect(screen.getByText('Hades')).toBeInTheDocument()
    expect(screen.getByText('Hades.exe')).toBeInTheDocument()
    expect(screen.getByText('45m')).toBeInTheDocument()
    expect(screen.getByText('PLAYING')).toBeInTheDocument()
  })

  it('should show "Never" for missing lastPlayDate', () => {
    const game: Game = {
      name: 'New Game',
      exeName: 'new.exe',
      playtimeMinutes: 0,
      sessionCount: 0,
      status: 'UNPLAYED',
      lastPlayDate: null
    }
    render(<GameItem game={game} />)
    expect(screen.getByText('Never')).toBeInTheDocument()
  })
})

import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import SummaryCard from './SummaryCard'

describe('SummaryCard', () => {
  it('should show loading state', () => {
    render(<SummaryCard summary={undefined} isLoading={true} />)
    expect(screen.getByText(/Loading summary/i)).toBeInTheDocument()
  })

  it('should render summary data', () => {
    const summary = {
      totalPlaytimeMinutes: 150,
      gamingPCName: 'Gaming-Rig',
      activeGameName: 'Hades',
      systemStats: {}
    }
    render(<SummaryCard summary={summary} isLoading={false} />)
    
    expect(screen.getByText(/Total Playtime: 150 min/i)).toBeInTheDocument()
    expect(screen.getByText(/PC: Gaming-Rig/i)).toBeInTheDocument()
    expect(screen.getByText(/Currently Playing:/i)).toBeInTheDocument()
    expect(screen.getByText(/Hades/i)).toBeInTheDocument()
  })

  it('should return null if no summary and not loading', () => {
    const { container } = render(<SummaryCard summary={undefined} isLoading={false} />)
    expect(container.firstChild).toBeNull()
  })
})

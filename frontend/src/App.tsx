import { useQuery } from '@tanstack/react-query';
import { gameService } from './services/api';
import SummaryCard from './components/SummaryCard';
import GameList from './components/GameList';
import './App.css';

function App() {
  const { data: summary, isLoading: isSummaryLoading } = useQuery({
    queryKey: ['summary'],
    queryFn: gameService.getSummary,
    refetchInterval: 5000,
  });

  const { data: games, isLoading: isGamesLoading } = useQuery({
    queryKey: ['games'],
    queryFn: gameService.getGames,
  });

  return (
    <div className="app-container">
      <header>
        <h1>Gaming Tracker</h1>
      </header>
      
      <main>
        <SummaryCard summary={summary} isLoading={isSummaryLoading} />
        <GameList games={games} isLoading={isGamesLoading} />
      </main>
      
      <footer>
        <p>Gaming Gaiden Rebirth &copy; 2026</p>
      </footer>
    </div>
  );
}

export default App;

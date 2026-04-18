import { useQuery } from '@tanstack/react-query';
import { gameService } from './services/api';
import SummaryCard from './components/SummaryCard';
import GameList from './components/GameList';
import AddGameForm from './components/AddGameForm';
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

  const { data: updateStatus } = useQuery({
    queryKey: ['updateCheck'],
    queryFn: gameService.checkUpdates,
    refetchInterval: 3600000, // 1 hour
  });

  return (
    <div className="app-container">
      {updateStatus?.hasUpdate && (
        <div className="update-banner">
          A new version ({updateStatus.latestVersion}) is available! 
          <a href={updateStatus.downloadUrl || '#'} target="_blank" rel="noreferrer"> Download now</a>
        </div>
      )}
      
      <header>
        <h1>Gaming Tracker</h1>
      </header>
      
      <main>
        <SummaryCard summary={summary} isLoading={isSummaryLoading} />
        <div className="main-content">
          <GameList games={games} isLoading={isGamesLoading} />
          <AddGameForm />
        </div>
      </main>
      
      <footer>
        <p>Gaming Tracker &copy; 2026 | Current Version: {updateStatus?.currentVersion || '1.0.0'}</p>
      </footer>
    </div>
  );
}

export default App;

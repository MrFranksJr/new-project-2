import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {useEffect, useState} from 'react';
import {gameService} from './services/api';
import SummaryCard from './components/SummaryCard';
import GameList from './components/GameList';
import AddGameForm from './components/AddGameForm';
import './App.css';

function App() {
  const [currentView, setCurrentView] = useState(window.location.hash || '#summary');

  useEffect(() => {
    const handleHashChange = () => {
      setCurrentView(window.location.hash || '#summary');
    };
    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  const [deleteDb, setDeleteDb] = useState(false);
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

  const queryClient = useQueryClient();
  const { data: autostart, isLoading: isAutostartLoading } = useQuery({
    queryKey: ['autostart'],
    queryFn: () => gameService.getAutostart(),
  });

  const toggleMutation = useMutation({
    mutationFn: (enabled: boolean) => gameService.toggleAutostart(enabled),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['autostart'] });
    },
  });

  const cleanupMutation = useMutation({
    mutationFn: (deleteDb: boolean) => gameService.cleanup(deleteDb),
  });

  return (
    <div className="app-container">
      {updateStatus?.hasUpdate && (
        <div className="update-banner">
          A new version ({updateStatus.latestVersion}) is available! 
          <a href={updateStatus.downloadUrl || '#'} target="_blank" rel="noreferrer"> Download now</a>
        </div>
      )}
      
      <header style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <h1>Gaming Tracker</h1>
        <label style={{display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer'}}>
          Autostart on login
          <input
            type="checkbox"
            checked={autostart?.enabled ?? false}
            onChange={(e) => toggleMutation.mutate(e.target.checked)}
            disabled={toggleMutation.isPending || isAutostartLoading}
          />
        </label>
      </header>
      
      <main>
        {currentView === '#uninstall' ? (
          <section>
            <h2>Uninstall Cleanup</h2>
            <label style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <input
                type="checkbox"
                checked={deleteDb}
                onChange={(e) => setDeleteDb(e.target.checked)}
              />
              Delete local database (`gaming-tracker.db`)
            </label>
            <button
              disabled={cleanupMutation.isPending}
              onClick={() => cleanupMutation.mutate(deleteDb)}
            >
              Run cleanup
            </button>
            {cleanupMutation.isSuccess && (
              <p>Cleanup done. Uninstall via Windows Settings &gt; Apps &amp; features.</p>
            )}
          </section>
        ) : currentView === '#add' ? (
          <section className="view-section">
            <AddGameForm />
          </section>
        ) : currentView === '#games' ? (
          <section className="view-section">
            <GameList games={games} isLoading={isGamesLoading} />
          </section>
        ) : (
          <>
            <SummaryCard summary={summary} isLoading={isSummaryLoading} />
            <div className="main-content">
              <GameList games={games} isLoading={isGamesLoading} />
              <AddGameForm />
            </div>
          </>
        )}
      </main>
      
      <footer>
        <p>Gaming Tracker &copy; 2026 | Current Version: {updateStatus?.currentVersion || '1.0.0'}</p>
      </footer>
    </div>
  );
}

export default App;

import type { Game } from '../types';
import GameItem from './GameItem';

interface GameListProps {
  games: Game[] | undefined;
  isLoading: boolean;
}

const GameList = ({ games, isLoading }: GameListProps) => {
  if (isLoading) {
    return <div className="game-list loading">Loading games...</div>;
  }

  if (!games || games.length === 0) {
    return <div className="game-list empty">No games tracked yet.</div>;
  }

  return (
    <div className="game-list">
      <h2>Tracked Games</h2>
      <div className="games-grid">
        {games.map((game) => (
          <GameItem key={game.name} game={game} />
        ))}
      </div>
    </div>
  );
};

export default GameList;

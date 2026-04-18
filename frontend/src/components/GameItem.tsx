import React from 'react';
import type { Game } from '../types';
import { Clock, Calendar } from 'lucide-react';

interface GameItemProps {
  game: Game;
}

const GameItem: React.FC<GameItemProps> = ({ game }) => {
  return (
    <div className={`game-item status-${game.status.toLowerCase()}`}>
      <div className="game-info">
        <h3>{game.name}</h3>
        <p className="exe-name">{game.exeName}</p>
      </div>
      <div className="game-stats">
        <div className="stat">
          <Clock size={16} />
          <span>{game.playtimeMinutes}m</span>
        </div>
        <div className="stat">
          <Calendar size={16} />
          <span>{game.lastPlayDate ? new Date(game.lastPlayDate).toLocaleDateString() : 'Never'}</span>
        </div>
      </div>
      <div className="game-status">
        {game.status}
      </div>
    </div>
  );
};

export default GameItem;

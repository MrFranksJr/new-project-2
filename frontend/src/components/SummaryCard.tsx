import React from 'react';
import { Monitor, Clock, Play } from 'lucide-react';
import type { Summary } from '../types';

interface SummaryCardProps {
  summary: Summary | undefined;
  isLoading: boolean;
}

const SummaryCard: React.FC<SummaryCardProps> = ({ summary, isLoading }) => {
  if (isLoading) {
    return <div className="summary-card loading">Loading summary...</div>;
  }

  if (!summary) return null;

  return (
    <div className="summary-card">
      <div className="summary-item">
        <Clock className="icon" size={20} />
        <span>Total Playtime: {summary.totalPlaytimeMinutes} min</span>
      </div>
      <div className="summary-item">
        <Monitor className="icon" size={20} />
        <span>PC: {summary.gamingPCName || 'Unknown'}</span>
      </div>
      {summary.activeGameName && (
        <div className="summary-item active">
          <Play className="icon" size={20} />
          <span>Currently Playing: <strong>{summary.activeGameName}</strong></span>
        </div>
      )}
    </div>
  );
};

export default SummaryCard;

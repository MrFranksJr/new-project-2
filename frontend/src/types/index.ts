export interface Game {
  name: string;
  exeName: string;
  playtimeMinutes: number;
  sessionCount: number;
  lastPlayDate: string | null;
  status: 'UNPLAYED' | 'PLAYING' | 'COMPLETED' | 'REPLAYING';
}

export interface GamesResponse {
  games: Game[];
}

export interface Summary {
  totalPlaytimeMinutes: number;
  activeGameName: string | null;
  gamingPCName: string | null;
  systemStats: Record<string, string>;
}

export interface UpdateStatus {
  hasUpdate: boolean;
  latestVersion: string;
  currentVersion: string;
  downloadUrl: string | null;
}

import axios from 'axios';
import type { Game, GamesResponse, Summary, UpdateStatus } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const gameService = {
  getGames: async (): Promise<Game[]> => {
    const response = await api.get<GamesResponse>('/games');
    return response.data.games;
  },

  addGame: async (name: string, exeName: string): Promise<Game> => {
    const response = await api.post<Game>('/games', { name, exeName });
    return response.data;
  },

  getSummary: async (): Promise<Summary> => {
    const response = await api.get<Summary>('/summary');
    return response.data;
  },

  checkUpdates: async (): Promise<UpdateStatus> => {
    const response = await api.get<UpdateStatus>('/update-check');
    return response.data;
  },
  getAutostart: async (): Promise<{ enabled: boolean }> => {
    const response = await api.get('/autostart');
    return response.data;
  },
  toggleAutostart: async (enabled: boolean): Promise<boolean> => {
    const response = await api.post('/autostart', { enabled });
    return response.data.enabled;
  },
};

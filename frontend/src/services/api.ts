import axios from 'axios';
import type { Game, GamesResponse, Summary } from '../types';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const gameService = {
  getGames: async (): Promise<Game[]> => {
    const response = await api.get<GamesResponse>('/games');
    return response.data.games;
  },

  getSummary: async (): Promise<Summary> => {
    const response = await api.get<Summary>('/summary');
    return response.data;
  },
};

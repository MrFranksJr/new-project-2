import React, { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { gameService } from '../services/api';

const AddGameForm: React.FC = () => {
  const [name, setName] = useState('');
  const [exeName, setExeName] = useState('');
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: () => gameService.addGame(name, exeName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['games'] });
      setName('');
      setExeName('');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name && exeName) {
      mutation.mutate();
    }
  };

  return (
    <div className="add-game-form">
      <h3>Add New Game</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="gameName">Game Name</label>
          <input
            id="gameName"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Elden Ring"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="exeName">Executable Name</label>
          <input
            id="exeName"
            type="text"
            value={exeName}
            onChange={(e) => setExeName(e.target.value)}
            placeholder="e.g. eldenring.exe"
            required
          />
        </div>
        <button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Adding...' : 'Add Game'}
        </button>
      </form>
    </div>
  );
};

export default AddGameForm;

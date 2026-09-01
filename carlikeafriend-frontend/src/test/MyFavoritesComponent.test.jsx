import React from 'react';
import { render, screen } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { MyFavoritesComponent } from '../components/MyFavoritesComponent';

// Mocks parciales limpios utilizando importOriginal
vi.mock('../context/UserContext', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        UserContext: React.createContext({ token: 'mock-token', logout: vi.fn() }),
    };
});

vi.mock('../context/MessageModalContext', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useMessageModal: () => ({ setModalMessage: vi.fn() }),
    };
});

vi.mock('../context/FavoriteContext', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        FavoriteContext: React.createContext({
            favorites: [],
            setFavorites: vi.fn(),
            setFavoritesIds: vi.fn(),
            isLoading: false,
            error: null,
            toggleFavorite: vi.fn(),
        }),
    };
});

describe('MyFavoritesComponent', () => {
    it('debe renderizar estado vacío cuando no hay favoritos', () => {
        render(<MyFavoritesComponent />);

        expect(screen.getByText('Tu lista está vacía')).toBeInTheDocument();
        expect(screen.getByText('Mis Vehículos Favoritos')).toBeInTheDocument();
    });
});
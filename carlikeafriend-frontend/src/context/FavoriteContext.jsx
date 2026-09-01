import { createContext, useState, useContext, useEffect, useCallback, useMemo } from 'react';
import { UserContext } from './UserContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from './MessageModalContext';
import { useNavigate } from 'react-router-dom';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const FavoriteContext = createContext();

export const FavoriteProvider = ({ children }) => {
    const { token, logout, isAuthenticated } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const [favorites, setFavorites] = useState([]);
    const [favoritesIds, setFavoritesIds] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // Cargar favoritos al iniciar o cuando el usuario se loguea
    const loadFavorites = useCallback(async () => {
        if (!isAuthenticated || !token) {
            setFavorites([]);
            setFavoritesIds([]);
            return;
        }
        setIsLoading(true);

        try {
            const response = await fetch(`${API_CONFIG.FAVORITES}/me`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

            if (response.ok) {
                const data = await response.json();
                setFavorites(data);
                setFavoritesIds(data.map(fav => fav.id));
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error cargando favoritos:", error);
        } finally {
            setIsLoading(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps -- NOTA: Se omiten navigate y setModalMessage porque son estables. Si agregas nuevas variables de estado a esta función, ¡DEBES añadirlas al arreglo manualmente!
    }, [isAuthenticated, token, logout]);

    useEffect(() => {
        loadFavorites();
    }, [loadFavorites]);

    // Función global para marcar/desmarcar
    const toggleFavorite = useCallback(async (product) => {
        if (!isAuthenticated) {
            setModalMessage("Debes iniciar sesión para agregar a favoritos.");
            navigate('/signin');
            return false;
        }

        const productId = product.id;

        try {
            const response = await fetch(API_CONFIG.FAVORITES, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ productId })
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

            if (response.ok) {
                // Verificamos si la acción fue AGREGAR o QUITAR basándonos en si ya existía el ID
                const isAdding = !favoritesIds.includes(productId);

                if (isAdding) {
                    // ACCIÓN: AGREGAR
                    setFavoritesIds(prev => [...prev, productId]);
                    setFavorites(prev => [...prev, product]); // ¡Agregamos el objeto completo!
                } else {
                    // ACCIÓN: QUITAR
                    setFavoritesIds(prev => prev.filter(id => id !== productId));
                    setFavorites(prev => prev.filter(fav => fav.id !== productId));
                }
                return true;
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al actualizar favorito:", error);
            const message = error.message.includes("Failed to fetch") ? "Error de conexión: Revisa tu internet." : error.message;
            setError(message || "Ocurrió un error inesperado")
            return false;
        }
    }, [isAuthenticated, token, favoritesIds, navigate, logout, setModalMessage]);

    // Función rápida para que la tarjeta sepa si es favorito
    const isProductFavorite = useCallback((productId) => {
        return favoritesIds.includes(productId);
    }, [favoritesIds]);

    const contextValue = useMemo(() => ({
        favorites,
        setFavorites,
        favoritesIds,
        setFavoritesIds,
        toggleFavorite,
        isProductFavorite,
        isLoading,
        error
    }), [favorites, favoritesIds, toggleFavorite, isProductFavorite, isLoading, error])

    return (
        <FavoriteContext.Provider value={contextValue}>
            {children}
        </FavoriteContext.Provider>
    );
};
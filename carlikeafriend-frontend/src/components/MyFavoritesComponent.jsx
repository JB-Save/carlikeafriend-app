import React, { useState, useContext } from 'react';
import { FavoriteCardComponent } from './FavoriteCardComponent';
import { FavoriteSkeleton } from './FavoriteSkeleton';
import { ToastNotification } from './ToastNotification';
import { Link, useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { UserContext } from '../context/UserContext';
import { FavoriteContext } from '../context/FavoriteContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';
import '../styles/MyAccountStyle.css'

export const MyFavoritesComponent = () => {
    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();
    const {
        favorites,
        setFavorites,
        setFavoritesIds,
        isLoading,
        error,
        toggleFavorite
    } = useContext(FavoriteContext);

    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState(null);
    const [type, setType] = useState("success");

    // 1. Eliminar un favorito
    const handleRemoveOne = async (product) => {
        setShowToast(false);

        const success = await toggleFavorite(product);

        if (success) {
            // Disparar Toast
            setToastMessage("Vehículo eliminado de tus favoritos");
            setType("success");
            setShowToast(true);
        } else {
            setToastMessage(error);
            setType("failure");
            setShowToast(true);
        }
    }


    // 2. Vaciar toda la lista
    const handleClearAll = async () => {
        setShowToast(false);

        try {
            const MY_FAVORITES_URL = `${API_CONFIG.FAVORITES}/me`;

            const response = await fetch(MY_FAVORITES_URL, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Manejo de seguridad (401)
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                setFavorites([]);
                setFavoritesIds([]);
                // Disparar Toast
                setToastMessage("Todos tus favoritos han sido eliminados");
                setType("success");
                setShowToast(true);
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al eliminar", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setToastMessage(message || "Ocurrió un error inesperado");
            setType("failure");
            setShowToast(true);
        }
    };

    return (

        <div id="my-favorite-content" className="w-100">
            {/* Encabezado Dinámico */}
            <div className="d-flex flex-column flex-sm-row gap-2 justify-content-between align-items-center mb-4 border-bottom pb-2">
                <h3 className="h4 fw-bold text-center text-sm-start favorite-block-title-color mb-0">
                    <i className="bi bi-heart-fill me-2 text-danger"></i> Mis Vehículos Favoritos
                </h3>

                <div className="d-flex align-items-center gap-2 flex-column flex-sm-row w-100 w-sm-auto">
                    <span className="badge bg-primary-subtle favorite-block-text-primary rounded-pill fs-6 px-3 py-2 w-100 w-sm-auto text-center">
                        {favorites.length} guardados
                    </span>
                    {!isLoading && favorites.length > 0 && (
                        <button onClick={handleClearAll} className="btn btn-outline-danger btn-sm rounded-pill px-3 py-1 w-100 w-sm-auto">
                            <i className="bi bi-trash3-fill me-1"></i> Vaciar lista
                        </button>
                    )}
                </div>
            </div>

            {/* Lógica de Renderizado */}
            {isLoading ? (
                <div className="row g-4">
                    {[1, 2, 3].map((n) => (
                        <div key={n} className="col-12 col-md-6 col-lg-4">
                            <FavoriteSkeleton />
                        </div>
                    ))}
                </div>
            ) : favorites.length === 0 ? (
                <div className="text-center py-5 rounded-4 bg-light border border-2 border-dashed">
                    <i className="bi bi-heartbreak favorite-block-text-muted display-3"></i>
                    <h4 className="mt-3 fw-bold favorite-block-title-color">Tu lista está vacía</h4>
                    <p className="favorite-block-text-muted small">Explora nuestro catálogo y añade los vehículos que más te gusten.</p>
                    <Link to="/" className="btn favorite-block-btn-detail btn-sm rounded-pill px-4 mt-2 shadow-sm">
                        Explorar Catálogo
                    </Link>
                </div>
            ) : (
                <div className="row g-4">
                    {favorites.map((fav) => (
                        <div key={fav.id} className="col-12 col-md-6 col-lg-4">
                            <FavoriteCardComponent
                                product={fav}
                                onRemove={() => handleRemoveOne(fav)}
                            />
                        </div>
                    ))}
                </div>
            )
            }
            {/* Componente Toast al final del fragmento */}
            <ToastNotification
                show={showToast}
                message={toastMessage}
                type={type}
                onClose={() => setShowToast(false)}
            />
        </div >
    );
}


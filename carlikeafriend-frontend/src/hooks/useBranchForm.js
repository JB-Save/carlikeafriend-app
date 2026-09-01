import { useState, useEffect, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useBranchForm = (branchToEdit, onBranchSaved) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [allCities, setAllCities] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de carga ciudades
    const [isLoadingCity, setIsLoadingCity] = useState(true);
    const [cityError, setCityError] = useState(null);


    useEffect(() => {

        const fetchCities = async () => {
            setIsLoadingCity(true);
            setCityError(null);

            try {

                const response = await fetch(API_CONFIG.CITIES, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                if (response.ok) {
                    const data = await response.json();
                    setAllCities(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }

            } catch (error) {
                console.error("Error cargando ciudades: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setCityError(message);
            } finally {
                setIsLoadingCity(false);
            }
        };

        if (token) fetchCities();

    }, [token, navigate, logout]);

    const submitBranchData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            cityId: Number(data.cityId)
        };

        try {

            const URL = branchToEdit
                ? `${API_CONFIG.BRANCHES}/${branchToEdit.id}`
                : API_CONFIG.BRANCHES;

            const method = branchToEdit ? 'PUT' : 'POST';

            const response = await fetch(URL, {
                method: method,
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

            if (response.ok) {
                if (onBranchSaved) onBranchSaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error guardando sucursal:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allCities,
        isLoadingCity,
        cityError,
        error,
        isSubmittingForm,
        submitBranchData
    };
}
import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useExtrasForm = (extrasToEdit, onExtrasSaved) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [allChargeTypes, setAllChargeTypes] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de tipo de cargo
    const [isLoadingChargeTypes, setIsLoadingChargeTypes] = useState(true);
    const [chargeTypeError, setChargeTypesError] = useState(null);

    useEffect(() => {

        const fetchChargeTypes = async () => {
            setIsLoadingChargeTypes(true);
            setChargeTypesError(null);

            try {

                const response = await fetch(API_CONFIG.CHARGE_TYPES, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                // Procesar Estado
                if (response.ok) {
                    const data = await response.json();
                    setAllChargeTypes(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg)
                }

            } catch (error) {
                console.error("Error cargando tipos de cargo: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setChargeTypesError(message || "Ocurrió un error inesperado");
            } finally {
                setIsLoadingChargeTypes(false);
            }

        };

        if (token) fetchChargeTypes();

    }, [token, navigate, logout]);


    const submitExtrasData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            currentPrice: Number(data.currentPrice),
            maxQuantityPerReservation: Number(data.maxQuantityPerReservation),
            maxChargeableDays: Number(data.maxChargeableDays)

        };

        try {

            const URL = extrasToEdit
                ? `${API_CONFIG.ADDONS}/${extrasToEdit.addonId}`
                : API_CONFIG.ADDONS;

            const method = extrasToEdit ? 'PUT' : 'POST';

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
                if (onExtrasSaved) onExtrasSaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error guardando extra:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allChargeTypes,
        isLoadingChargeTypes,
        chargeTypeError,
        error,
        isSubmittingForm,
        submitExtrasData
    };
}
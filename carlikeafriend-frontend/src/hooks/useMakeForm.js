import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useMakeForm = (makeToEdit, onMakeSaved) => {

    const { token, logout } = useContext(UserContext); // Obtener token
    const { setModalMessage } = useMessageModal();
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const submitMakeData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data
        };

        try {

            const URL = makeToEdit
                ? `${API_CONFIG.MAKES}/${makeToEdit.id}`
                : API_CONFIG.MAKES;

            const method = makeToEdit ? 'PUT' : 'POST';

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
                if (onMakeSaved) onMakeSaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error guardando marca: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        error,
        isSubmittingForm,
        submitMakeData
    };
}
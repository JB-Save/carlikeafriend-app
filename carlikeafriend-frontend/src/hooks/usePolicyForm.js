import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const usePolicyForm = (policyToEdit, onPolicySaved) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [allPolicyTypes, setAllPolicyTypes] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de carga tipo de políticas
    const [isLoadingPolicyType, setIsLoadingPolicyType] = useState(true);
    const [policyTypeError, setPolicyTypeError] = useState(null);


    useEffect(() => {

        const fetchPolicyTypes = async () => {
            setIsLoadingPolicyType(true);
            setPolicyTypeError(null);

            try {
                // Lanzamos ambas peticiones en paralelo
                const response = await fetch(API_CONFIG.POLICY_TYPES, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

                if (response.ok) {
                    const data = await response.json();
                    setAllPolicyTypes(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg)
                }
            } catch (error) {
                console.error("Error cargando tipo de políticas:", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setPolicyTypeError(message || "Ocurrió un error inesperado");
            } finally {
                setIsLoadingPolicyType(false);
            }
        };

        if (token) fetchPolicyTypes();

    }, [token, navigate, logout]);


    const submitPolicyData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            policyTypeId: Number(data.policyTypeId)
        };

        try {

            const URL = policyToEdit
                ? `${API_CONFIG.POLICIES}/${policyToEdit.id}`
                : API_CONFIG.POLICIES;

            const method = policyToEdit ? 'PUT' : 'POST';

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
                if (onPolicySaved) onPolicySaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error guardando política:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allPolicyTypes,
        isLoadingPolicyType,
        policyTypeError,
        error,
        isSubmittingForm,
        submitPolicyData
    };
}
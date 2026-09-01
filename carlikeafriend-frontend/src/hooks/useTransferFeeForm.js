import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useTransferFeeForm = (transferFeeToEdit, onTransferFeeSaved) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [allBranches, setAllBranches] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de carga sucursales
    const [isLoadingBranch, setIsLoadingBranch] = useState(true);
    const [branchError, setBranchError] = useState(null);


    useEffect(() => {

        const fetchBranches = async () => {
            setIsLoadingBranch(true);
            setBranchError(null);

            try {

                const response = await fetch(API_CONFIG.BRANCHES, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });


                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) {
                    return;
                }

                if (response.ok) {
                    const data = await response.json();
                    setAllBranches(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }

            } catch (error) {
                console.error("Error cargando sucursales: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setBranchError(message || "Ocurrió un error inesperado");
            } finally {
                setIsLoadingBranch(false);
            }

        };

        if (token) fetchBranches();

    }, [token, navigate, logout]);

    const submitFeeData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            originBranchId: Number(data.originBranchId),
            destinationBranchId: Number(data.destinationBranchId),
            feeAmount: Number(data.feeAmount)
        };

        try {

            const URL = transferFeeToEdit
                ? `${API_CONFIG.TRANSFER_FEES}/${transferFeeToEdit.id}`
                : API_CONFIG.TRANSFER_FEES;

            const method = transferFeeToEdit ? 'PUT' : 'POST';

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
                if (onTransferFeeSaved) onTransferFeeSaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error guardando tarifa:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allBranches,
        isLoadingBranch,
        branchError,
        error,
        isSubmittingForm,
        submitFeeData
    };
}
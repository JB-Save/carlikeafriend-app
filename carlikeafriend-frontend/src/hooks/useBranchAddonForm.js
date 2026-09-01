import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useBranchAddonForm = (onInventoryAssigned) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    // Estados para los catálogos
    const [allBranches, setAllBranches] = useState([]);
    const [allAddons, setAllAddons] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de carga sucursales
    const [isLoadingBranch, setIsLoadingBranch] = useState(true);
    const [branchError, setBranchError] = useState(null);

    //Estados de carga extras
    const [isLoadingAddon, setIsLoadingAddon] = useState(true);
    const [addonError, setAddonError] = useState(null);

    useEffect(() => {
        const loadCatalogs = async () => {
            setIsLoadingBranch(true);
            setIsLoadingAddon(true);
            setBranchError(null);
            setAddonError(null);

            try {
                // Peticiones en paralelo
                const [resBranch, resAddon] = await Promise.all([
                    fetch(API_CONFIG.BRANCHES, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${token}` }
                    }),
                    fetch(API_CONFIG.ADDONS, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${token}` }
                    })
                ]);

                if (handleUnauthorizedError(resBranch, navigate, logout, setModalMessage) ||
                    handleUnauthorizedError(resAddon, navigate, logout, setModalMessage)) {
                    return;
                }

                // Procesar Sucursales
                if (resBranch.ok) {
                    const branchData = await resBranch.json();
                    setAllBranches(branchData);
                } else {
                    const branchMsg = await extractErrorMessage(resBranch);
                    setBranchError(branchMsg);
                }

                // Procesar Extras
                if (resAddon.ok) {
                    const addonData = await resAddon.json();
                    setAllAddons(addonData);
                } else {
                    const branchMsg = await extractErrorMessage(resAddon);
                    setAddonError(branchMsg);
                }

            } catch (error) {
                console.error("Error de red en carga inicial:", error);
                const networkMsg = "No se pudo establecer conexión con el servidor.";
                setBranchError(networkMsg);
                setAddonError(networkMsg);
            } finally {
                setIsLoadingBranch(false);
                setIsLoadingAddon(false);
            }
        };

        if (token) loadCatalogs();

    }, [token, navigate, logout]);


    const submitInventoryData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            branchId: Number(data.branchId),
            addonId: Number(data.addonId),
            totalStock: Number(data.totalStock)
        };

        try {
            const response = await fetch(API_CONFIG.BRANCH_INVENTORY, {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                if (onInventoryAssigned) onInventoryAssigned();
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error asignando inventario:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado.");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allBranches,
        allAddons,
        isLoadingBranch,
        isLoadingAddon,
        branchError,
        addonError,
        error,
        isSubmittingForm,
        submitInventoryData
    };
}
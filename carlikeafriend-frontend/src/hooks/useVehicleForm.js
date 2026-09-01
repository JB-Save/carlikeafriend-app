import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useVehicleForm = (vehicleToEdit, onVehicleSaved) => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [allProducts, setAllProducts] = useState([]);
    const [allBranches, setAllBranches] = useState([]);
    const [allVehicleStatus, setAllVehicleStatus] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmittingForm, setIsSubmittingForm] = useState(false);

    //Estados de carga productos
    const [isLoadingProduct, setIsLoadingProduct] = useState(true);
    const [productError, setProductError] = useState(null);

    //Estados de carga sucursales
    const [isLoadingBranch, setIsLoadingBranch] = useState(true);
    const [branchError, setBranchError] = useState(null);

    //Estados de carga estado vehículo
    const [isLoadingVehicleStatus, setIsLoadingVehicleStatus] = useState(true);
    const [vehicleStatusError, setVehicleStatusError] = useState(null);



    useEffect(() => {

        const loadFormData = async () => {
            setIsLoadingProduct(true);
            setIsLoadingBranch(true);
            setIsLoadingVehicleStatus(true);
            setProductError(null);
            setBranchError(null);
            setVehicleStatusError(null);
            try {
                // Lanzamos ambas peticiones en paralelo
                const [resProduct, resBranch, resStatus] = await Promise.all([
                    fetch(API_CONFIG.PRODUCTS, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${token}` }
                    }),
                    fetch(API_CONFIG.BRANCHES, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${token}` }
                    }),
                    fetch(API_CONFIG.VEHICLE_STATUSES, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${token}` }
                    })
                ]);

                // Solo necesitamos verificar la autorización UNA VEZ (si una da 401, la otra probablemente también)
                if (handleUnauthorizedError(resProduct, navigate, logout, setModalMessage) ||
                    handleUnauthorizedError(resBranch, navigate, logout, setModalMessage) ||
                    handleUnauthorizedError(resStatus, navigate, logout, setModalMessage)) {
                    return;
                }

                // Procesar Productos
                if (resProduct.ok) {
                    const productData = await resProduct.json();
                    setAllProducts(productData);
                } else {
                    const productMsg = await extractErrorMessage(resProduct);
                    setProductError(productMsg);
                }

                // Procesar Sucursales
                if (resBranch.ok) {
                    const branchData = await resBranch.json();
                    setAllBranches(branchData);
                } else {
                    const branchMsg = await extractErrorMessage(resBranch);
                    setBranchError(branchMsg);
                }

                // Procesar Estado Vehículo
                if (resStatus.ok) {
                    const statusData = await resStatus.json();
                    setAllVehicleStatus(statusData);
                } else {
                    const statusMsg = await extractErrorMessage(resStatus);
                    setVehicleStatusError(statusMsg);
                }

            } catch (error) {
                console.error("Error de red en carga inicial:", error);
                // Errores de conexión (cuando no hay respuesta del servidor)
                const networkMsg = "No se pudo establecer conexión con el servidor.";
                setProductError(networkMsg);
                setBranchError(networkMsg);
                setVehicleStatusError(networkMsg);
            } finally {
                setIsLoadingProduct(false);
                setIsLoadingBranch(false);
                setIsLoadingVehicleStatus(false);
            }

        };

        if (token) loadFormData();

    }, [token, navigate, logout]);

    const submitVehicleData = async (data) => {
        setIsSubmittingForm(true);
        setError(null);

        const payload = {
            ...data,
            productId: Number(data.productId),
            currentBranchId: Number(data.currentBranchId)
        };

        try {

            const URL = vehicleToEdit
                ? `${API_CONFIG.VEHICLES}/${vehicleToEdit.id}`
                : API_CONFIG.VEHICLES;

            const method = vehicleToEdit ? 'PUT' : 'POST';

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
                if (onVehicleSaved) onVehicleSaved();
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error guardando vehículo:", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setError(message || "Ocurrió un error inesperado");
        } finally {
            setIsSubmittingForm(false);
        }
    };

    return {
        allProducts,
        allBranches,
        allVehicleStatus,
        isLoadingProduct,
        isLoadingBranch,
        isLoadingVehicleStatus,
        productError,
        branchError,
        vehicleStatusError,
        error,
        isSubmittingForm,
        submitVehicleData
    };
}
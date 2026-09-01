import { useContext, useEffect, useState } from 'react';
import { BranchAddonTableComponent } from './BranchAddonTableComponent';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const BranchAddonListComponent = () => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    // Estados
    const [allBranches, setAllBranches] = useState([]);
    const [selectedBranchId, setSelectedBranchId] = useState('');
    const [inventory, setInventory] = useState([]);

    // Estados de carga y error
    const [isLoadingBranches, setIsLoadingBranches] = useState(true);
    const [isLoadingInventory, setIsLoadingInventory] = useState(false);
    const [err, setErr] = useState(null);

    // Endpoints
    const BRANCHES_URL = API_CONFIG.BRANCHES;
    const INVENTORY_URL = API_CONFIG.BRANCH_INVENTORY;

    // 1. Cargar la lista de sucursales al montar el componente
    useEffect(() => {
        const fetchBranches = async () => {
            setIsLoadingBranches(true);
            setErr(null);
            try {
                const response = await fetch(BRANCHES_URL, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                if (response.ok) {
                    const data = await response.json();
                    setAllBranches(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }
            } catch (error) {
                console.error("Error al obtener sucursales: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setErr(message || "Ocurrió un error inesperado.");
            } finally {
                setIsLoadingBranches(false);
            }
        };

        if (token) fetchBranches();
    }, [token, navigate, logout]);

    // 2. Cargar el inventario cuando se selecciona una sucursal
    useEffect(() => {
        const fetchInventory = async () => {
            if (!selectedBranchId) {
                setInventory([]);
                return;
            }

            setIsLoadingInventory(true);
            setErr(null);
            try {
                const response = await fetch(`${INVENTORY_URL}?branchId=${selectedBranchId}`, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                if (response.ok) {
                    const data = await response.json();
                    setInventory(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }
            } catch (error) {
                console.error("Error al obtener inventario: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setErr(message || "Ocurrió un error inesperado.");
            } finally {
                setIsLoadingInventory(false);
            }
        };

        fetchInventory();
    }, [selectedBranchId, token, navigate, logout]);

    const handleBranchChange = (e) => {
        setSelectedBranchId(e.target.value);
    };

    return (
        <div id="branch-addon-list-content" className="w-100 py-2">

            <div className="d-flex justify-content-center justify-content-md-end mb-3">
                <Link to="/administration/assign-addon" className="text-decoration-none py-2 px-2 rounded-3 btn btn-add text-center">
                    <i className="bi bi-plus-circle-fill me-2"></i> Asignar Nuevo Extra
                </Link>
            </div>

            <h2 className="h3 fw-bold text-list text-center mb-4">Inventario de Extras por Sucursal</h2>

            {err && <div className="alert alert-danger text-center">{err}</div>}

            <div className="card custom-card-shadow rounded-3 p-4">

                {/* Selector de Sucursal */}
                <div className="mb-4 col-md-6 mx-auto">
                    <label htmlFor="branchSelect" className="form-label fw-bold">Seleccione una Sucursal:</label>
                    {isLoadingBranches ? (
                        <div className="spinner-border spinner-border-sm ms-2" role="status"></div>
                    ) : (
                        <select
                            id="branchSelect"
                            className="form-select"
                            value={selectedBranchId}
                            onChange={handleBranchChange}
                        >
                            <option value="">-- Seleccione para ver inventario --</option>
                            {allBranches.map(branch => (
                                <option key={branch.id} value={branch.id}>{branch.name}</option>
                            ))}
                        </select>
                    )}
                </div>

                {/* Tabla de Inventario */}
                {selectedBranchId && (
                    <>
                        {isLoadingInventory ? (
                            <div className="text-center my-5">
                                <div className="spinner-border" role="status"></div>
                                <p className="mt-2 admin-panel-text-muted">Cargando inventario...</p>
                            </div>
                        ) : (!inventory || inventory.length === 0) ? (
                            <div className="alert alert-warning text-center">
                                Esta sucursal no tiene extras asignados en su inventario.
                            </div>
                        ) : (
                            <BranchAddonTableComponent
                                inventory={inventory}
                            />
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
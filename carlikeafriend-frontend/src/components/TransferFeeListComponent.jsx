import { useContext, useEffect, useState } from 'react'
import { TransferFeeTableComponent } from './TransferFeeTableComponent';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const TransferFeeListComponent = () => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const [allBranches, setAllBranches] = useState([]);
    const [selectedBranchId, setSelectedBranchId] = useState('');
    const [branchFees, setBranchFees] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [err, setErr] = useState(null);
    const [isLoadingBranchFees, setIsLoadingBranchFees] = useState(false);

    const [isDeleting, setIsDeleting] = useState(false);

    const [errDelete, setErrDelete] = useState(null);
    const [transferFeeIdToDelete, setTransferFeeIdToDelete] = useState(null)

    const BRANCHES_URL = API_CONFIG.BRANCHES;

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    //Función para cargar las sucursales
    useEffect(() => {
        const fetchBranches = async () => {
            setIsLoading(true);
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
                setErr(message || "Ocurrió un error inesperado al cargar sucursales.");
            } finally {
                setIsLoading(false);
            }
        };

        if (token) fetchBranches();
    }, [token, navigate, logout]);

    //Función para cargar las tarifas por sucursal
    useEffect(() => {
        const fetchBranchFees = async () => {
            if (!selectedBranchId) {
                setBranchFees([]);
                return;
            }

            setIsLoadingBranchFees(true);
            setErr(null);
            try {
                const response = await fetch(`${BRANCHES_URL}/${selectedBranchId}/transfer-fees`, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                if (response.ok) {
                    const data = await response.json();
                    setBranchFees(data);
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }
            } catch (error) {
                console.error("Error al obtener inventario: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setErr(message || "Ocurrió un error inesperado al cargar el inventario.");
            } finally {
                setIsLoadingBranchFees(false);
            }
        };

        fetchBranchFees();
    }, [selectedBranchId, token, navigate, logout]);

    const handleBranchChange = (e) => {
        setSelectedBranchId(e.target.value);
    };

    useEffect(() => {
        if (errDelete) {
            const timer = setTimeout(() => {
                setErrDelete(null);
            }, 3000);
            // Limpieza: si el usuario vuelve a borrar algo o cierra el componente, 
            // cancelamos el timer anterior para evitar conflictos.
            return () => clearTimeout(timer);
        }
    }, [errDelete]);

    // Lógica de eliminación
    const deleteFunction = async (transferFeeIdToDelete) => {
        setErrDelete(null);
        setIsDeleting(true);

        try {
            const response = await fetch(`${BRANCHES_URL}/${transferFeeIdToDelete}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Manejo de seguridad (401)
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            // Si la respuesta es exitosa (204 No Content para DELETE)
            if (response.ok) {
                setErrDelete("Tarifa eliminada exitosamente.");
                fetchTransferFees();
            } else {
                // Si llegamos aquí, el servidor respondió con error (400, 403, 404, 500)
                // Lanzamos un error con el mensaje extraído
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error al eliminar tarifa: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setErrDelete(message || "Ocurrió un error inesperado.");
        } finally {
            setIsDeleting(false);
            setTransferFeeIdToDelete(null);
        }
    };

    return (
        <div id="transferFee-list-content" className="w-100 py-2"> {/*Contenido principal de la lista  */}
            <div className="d-flex justify-content-center justify-content-md-end mb-3">
                <Link to="/administration/add-transferFee" className="text-decoration-none py-2 px-2 rounded-3 btn btn-add text-center">
                    <i className="bi bi-plus-circle-fill me-2"></i> Agregar Tarifa
                </Link>
            </div>
            <h2 className="h3 fw-bold text-list text-center mb-4">Lista Tarifa de Transferencias</h2>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {errDelete && <div className={`alert ${errDelete.includes("exitosamente") ? 'alert-success' : 'alert-danger'}  text-center fade show`} style={{ transition: 'opacity 0.5s ease-in-out' }}>{errDelete}</div>}
            <div className="card custom-card-shadow rounded-3 p-4">

                {/* Selector de Sucursal */}
                <div className="mb-4 col-md-6 mx-auto">
                    <label htmlFor="branchSelect" className="form-label fw-bold">Seleccione una Sucursal de Origen:</label>
                    {isLoading ? (
                        <div className="spinner-border spinner-border-sm ms-2" role="status"></div>
                    ) : (
                        <select
                            id="branchSelect"
                            className="form-select"
                            value={selectedBranchId}
                            onChange={handleBranchChange}
                        >
                            <option value="">-- Seleccione para ver tarifas --</option>
                            {allBranches.map(branch => (
                                <option key={branch.id} value={branch.id}>{branch.name}</option>
                            ))}
                        </select>
                    )}
                </div>

                {/* Tabla de Inventario */}
                {selectedBranchId && (
                    <>
                        {isLoadingBranchFees ? (
                            <div className="text-center my-5">
                                <div className="spinner-border" role="status"></div>
                                <p className="mt-2 admin-panel-text-muted">Cargando tarifas...</p>
                            </div>
                        ) : (!branchFees || branchFees.length === 0) ? (
                            <div className="alert alert-warning text-center">
                                Esta sucursal no tiene tarifas asignadas.
                            </div>
                        ) : (
                            <TransferFeeTableComponent
                                transferFees={branchFees} setTransferFeeIdToDelete={setTransferFeeIdToDelete}
                            />
                        )}
                    </>
                )}

            </div>
            {/* El modal de confirmación se renderiza condicionalmente aquí */}
            {transferFeeIdToDelete && (
                <DeleteConfirmationModalComponent
                    id={transferFeeIdToDelete}
                    deleteFunction={deleteFunction}
                    onClose={() => setTransferFeeIdToDelete(null)}
                    objectName="esta tarifa"
                    isDeleting={isDeleting}
                />
            )}
        </div>
    );
}

import { useContext, useEffect, useState } from 'react'
import { PolicyTableComponent } from './PolicyTableComponent';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const PolicyListComponent = () => {

    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const [allPolicies, setAllPolicies] = useState([]);
    const navigate = useNavigate();

    const [isLoading, setIsLoading] = useState(true);
    const [err, setErr] = useState(null);

    const [isDeleting, setIsDeleting] = useState(false);

    const [errDelete, setErrDelete] = useState(null);
    const [policyIdToDelete, setPolicyIdToDelete] = useState(null)
    const URL = API_CONFIG.POLICIES;


    //Función para cargar las políticas
    const fetchPolicies = async () => {
        setIsLoading(true);
        setErr(null);

        try {

            const response = await fetch(URL, {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Si es 401, redirige y corta la ejecución aquí
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                const data = await response.json();
                setAllPolicies(data);
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al obtener políticas: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setErr(message || "Ocurrió un error inesperado.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (token) fetchPolicies();
    }, [token, navigate, logout]);

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
    const deleteFunction = async (policyIdToDelete) => {
        setErrDelete(null);
        setIsDeleting(true);

        try {
            const response = await fetch(`${URL}/${policyIdToDelete}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Manejo de seguridad (401)
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            // Si la respuesta es exitosa (204 No Content para DELETE)
            if (response.ok) {
                setErrDelete("Política eliminada exitosamente.");
                fetchPolicies();
            } else {
                // Si llegamos aquí, el servidor respondió con error (400, 403, 404, 500)
                // Lanzamos un error con el mensaje extraído
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("Error al eliminar política: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setErrDelete(message || "Ocurrió un error inesperado.");
        } finally {
            setIsDeleting(false);
            setPolicyIdToDelete(null);
        }
    };

    return (
        <div id="policy-list-content" className="w-100 py-2"> {/*Contenido principal de la lista  */}
            <div className="d-flex justify-content-center justify-content-md-end mb-3">
                <Link to="/administration/add-policy" className="text-decoration-none py-2 px-2 rounded-3 btn btn-add text-center">
                    <i className="bi bi-plus-circle-fill me-2"></i> Agregar Política
                </Link>
            </div>
            <h2 className="h3 fw-bold text-list text-center mb-4">Lista de Políticas</h2>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {errDelete && <div className={`alert ${errDelete.includes("exitosamente") ? 'alert-success' : 'alert-danger'}  text-center fade show`} style={{ transition: 'opacity 0.5s ease-in-out' }}>{errDelete}</div>}
            <div className="card custom-card-shadow rounded-3 p-4">

                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border" role="status"></div>
                        <p className="mt-2 admin-panel-text-muted">Cargando políticas...</p>
                    </div>
                ) : (!allPolicies || allPolicies.length === 0) ? (
                    <div className="text-center admin-panel-text-muted mb-3">No hay políticas disponibles.</div>
                ) : (
                    <PolicyTableComponent policies={allPolicies} setPolicyIdToDelete={setPolicyIdToDelete} />
                )
                }

            </div>
            {/* El modal de confirmación se renderiza condicionalmente aquí */}
            {policyIdToDelete && (
                <DeleteConfirmationModalComponent
                    id={policyIdToDelete}
                    deleteFunction={deleteFunction}
                    onClose={() => setPolicyIdToDelete(null)}
                    objectName="esta política"
                    isDeleting={isDeleting}
                />
            )}
        </div>
    );
}

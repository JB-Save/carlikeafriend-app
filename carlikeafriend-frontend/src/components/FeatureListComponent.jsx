import { useContext, useEffect, useState } from 'react'
import { FeatureTableComponent } from './FeatureTableComponent';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';

export const FeatureListComponent = () => {

    const { token, logout } = useContext(UserContext); // Obtener token
    const { setModalMessage } = useMessageModal();
    const [allFeatures, setAllFeatures] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [err, setErr] = useState(null);

    const [isDeleting, setIsDeleting] = useState(false);

    const [errDelete, setErrDelete] = useState(null);
    const [featureIdToDelete, setFeatureIdToDelete] = useState(null);
    const URL = API_CONFIG.FEATURES;

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const fetchFeatures = async () => {
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
                setAllFeatures(data);
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al obtener características: ", error);
            const message = error.message.includes("Failed to fetch") ? "Error de conexión: Revisa tu internet." : error.message;
            setErr(message || "Ocurrió un error inesperado.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (token) fetchFeatures();
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
    const deleteFunction = async (featureIdToDelete) => {
        setErrDelete(null);
        setIsDeleting(true);

        try {
            const response = await fetch(`${URL}/${featureIdToDelete}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Manejo de seguridad (401)
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                setErrDelete("Característica eliminada exitosamente.");
                fetchFeatures(); // Recargar la lista
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al eliminar característica: ", error);
            const message = error.message.includes("Failed to fetch") ? "Error de conexión: Revisa tu internet." : error.message;
            setErrDelete(message || "Ocurrió un error inesperado.");
        } finally {
            setIsDeleting(false);
            setFeatureIdToDelete(null);
        }
    };

    return (
        <div id="feature-list-content" className="w-100 py-2"> {/*Contenido principal de la lista de características  */}
            <div className="d-flex justify-content-center justify-content-md-end mb-3">
                <Link to="/administration/add-feature" className="text-decoration-none py-2 px-2 rounded-3 btn btn-add text-center">
                    <i className="bi bi-plus-circle-fill me-2"></i> Agregar Característica
                </Link>
            </div>
            <h2 className="h3 fw-bold text-list text-center mb-4">Lista de Características Disponibles</h2>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {errDelete && <div className={`alert ${errDelete.includes("exitosamente") ? 'alert-success' : 'alert-danger'}  text-center fade show`} style={{ transition: 'opacity 0.5s ease-in-out' }}>{errDelete}</div>}
            <div className="card custom-card-shadow rounded-3 p-4">
                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border" role="status"></div>
                        <p className="mt-2 admin-panel-text-muted">Cargando características...</p>
                    </div>
                ) : (!allFeatures || allFeatures.length === 0) ? (
                    <div className="text-center admin-panel-text-muted mb-3">No hay características disponibles.</div>
                ) : (
                    <FeatureTableComponent features={allFeatures} setFeatureIdToDelete={setFeatureIdToDelete} />
                )
                }

            </div>
            {/* El modal de confirmación se renderiza condicionalmente aquí */}
            {featureIdToDelete && (
                <DeleteConfirmationModalComponent
                    id={featureIdToDelete}
                    deleteFunction={deleteFunction}
                    onClose={() => setFeatureIdToDelete(null)}
                    objectName="esta característica"
                    isDeleting={isDeleting}
                />
            )}
        </div>
    );
}

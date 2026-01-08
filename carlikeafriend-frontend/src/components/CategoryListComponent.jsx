import { useContext, useEffect, useState } from 'react'
import { CategoryTableComponent } from './CategoryTableComponent';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const CategoryListComponent = () => {

    const { token, logout } = useContext(UserContext); // Obtener token
    const { setModalMessage } = useMessageModal();
    const [allCategories, setAllCategories] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [err, setErr] = useState(null);

    const [isDeleting, setIsDeleting] = useState(false);

    const [errDelete, setErrDelete] = useState(null);
    const [categoryIdToDelete, setCategoryIdToDelete] = useState(null)

    const URL = API_CONFIG.CATEGORIES;

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const fetchCategories = async () => {
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
                setAllCategories(data);
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al obtener categorías: ", error);
            setErr(error.message || "Ocurrió un error inesperado.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (token) fetchCategories();
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
    const deleteFunction = async (categoryIdToDelete) => {
        setErrDelete(null);
        setIsDeleting(true);

        try {
            const response = await fetch(`${URL}/${categoryIdToDelete}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Manejo de seguridad (401)
            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            // Si la respuesta es exitosa (204 No Content para DELETE)
            if (response.ok) {
                setErrDelete("Categoría eliminada exitosamente.");
                fetchCategories(); // Recargar la lista
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al eliminar categoría: ", error);
            setErrDelete(error.message || "Ocurrió un error inesperado.");
        } finally {
            setIsDeleting(false);
            setCategoryIdToDelete(null);
        }
    };

    return (
        <div id="category-list-content" className="container-fluid py-2"> {/*Contenido principal de la lista de categorías  */}
            <Link to="/administration/add-category" className="text-decoration-none d-block py-2 px-2 rounded btn-add text-center">
                <i className="bi bi-plus-circle-fill me-2"></i> Agregar Categoría
            </Link>
            <h2 className="h3 fw-bold text-list text-center mt-2">Lista de Categorías Disponibles</h2>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {errDelete && <div className={`alert ${errDelete.includes("exitosamente") ? 'alert-success' : 'alert-danger'}  text-center fade show`} style={{ transition: 'opacity 0.5s ease-in-out' }}>{errDelete}</div>}
            <div className="card card-shadow rounded-3 p-4">
                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border text-primary" role="status"></div>
                        <p className="mt-2 text-muted">Cargando categorías...</p>
                    </div>
                ) : (!allCategories || allCategories.length === 0) ? (
                    <div className="text-center text-muted mb-3">No hay categorías disponibles.</div>
                ) : (
                    <CategoryTableComponent categories={allCategories} setCategoryIdToDelete={setCategoryIdToDelete} />
                )
                }

            </div>
            {/* El modal de confirmación se renderiza condicionalmente aquí */}
            {categoryIdToDelete && (
                <DeleteConfirmationModalComponent
                    id={categoryIdToDelete}
                    deleteFunction={deleteFunction}
                    onClose={() => setCategoryIdToDelete(null)}
                    objectName="esta categoría"
                    isDeleting={isDeleting}
                />
            )}
        </div>
    );
}

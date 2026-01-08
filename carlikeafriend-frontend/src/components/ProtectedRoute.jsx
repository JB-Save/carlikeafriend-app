import { useContext } from "react"
import { UserContext } from "../context/UserContext"
import { Navigate, Outlet, useLocation } from "react-router-dom";

export const ProtectedRoute = ({ allowedRoles }) => {
    const { isAuthenticated, user } = useContext(UserContext);
    const location = useLocation(); // Obtenemos la ubicación actual y su estado

    // Verificar autenticación
    if (!isAuthenticated) {
        return <Navigate
            to="/signin"
            replace
            state={{ from: location }} />;
    }

    // Verificar autorización (si se pasaron roles permitidos)
    if (allowedRoles) {
        // Obtener los nombres de los roles del usuario
        // Asegurarse de que user.roles exista
        const userRoleNames = user?.roles?.map(role => role.name.toUpperCase()) || [];

        // Convertir los roles permitidos en un array
        const allowedRolesArray = allowedRoles.split(',').map(role => role.trim().toUpperCase());

        // Comprobar si el usuario tiene AL MENOS UNO de los roles permitidos
        const isAuthorized = userRoleNames.some(userRole => allowedRolesArray.includes(userRole));

        if (!isAuthorized) {
            // Si no está autorizado, redirigir a la página de inicio
            return <Navigate to="/" replace />;
        }
    }

    // Si está autenticado y (si aplica) autorizado, muestra la ruta
    return <Outlet />;
};

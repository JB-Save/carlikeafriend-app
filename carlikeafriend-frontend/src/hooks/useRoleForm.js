import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';

export const useRoleForm = (roleToEdit, onRoleSaved) => {

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const [allPermissions, setAllPermissions] = useState([]);
  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);

  // Manejar la carga de permisos manualmente
  const [isLoadingPermission, setIsLoadingPermission] = useState(true);
  const [permissionError, setPermissionError] = useState(null);

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  useEffect(() => {
    const fetchPermissions = async () => {
      setIsLoadingPermission(true);
      setPermissionError(null);

      try {

        const response = await fetch(API_CONFIG.PERMISSIONS, {
          method: 'GET',
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

        if (response.ok) {
          const data = await response.json();
          setAllPermissions(data);
        } else {
          // Manejo de otros errores (400, 500, etc.)
          const msg = await extractErrorMessage(response);
          throw new Error(msg);
        }
      } catch (error) {
        console.error("Error cargando datos del formulario: ", error);
        const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
        setPermissionError(message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingPermission(false);
      }
    };

    if (token) fetchPermissions();

  }, [token, navigate, logout]);

  const submitRoleData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);

    const payload = {
      ...data,
      permissions: data.permissions.map(id => Number(id))
    };

    try {

      const URL = roleToEdit
        ? `${API_CONFIG.ROLES}/${roleToEdit.id}`
        : API_CONFIG.ROLES;

      const method = roleToEdit ? 'PUT' : 'POST';

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
        if (onRoleSaved) onRoleSaved();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando rol:", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    allPermissions,
    isLoadingPermission,
    permissionError,
    error,
    isSubmittingForm,
    submitRoleData
  };
}
import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const usePermissionForm = (permissionToEdit, onPermissionSaved) => {
  const [permissionData, setPermissionData] = useState({
    name: '',
    description: ''
  });

  const { token, logout } = useContext(UserContext); // Obtener token
  const { setModalMessage } = useMessageModal();
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  useEffect(() => {
    if (permissionToEdit) {
      setPermissionData({
        name: permissionToEdit.name,
        description: permissionToEdit.description
      });
    }
  }, [permissionToEdit]);

  // Maneja cambios en los campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    setPermissionData(prevData => ({ ...prevData, [name]: value }));
  };

  // Función para resetear el formulario.
  const resetForm = useCallback(() => {
    setPermissionData({
      name: '',
      description: ''
    });
  }, []);

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      let response;
      const URL = permissionToEdit
        ? `${API_CONFIG.PERMISSIONS}/${permissionToEdit.id}`
        : API_CONFIG.PERMISSIONS;

      const method = permissionToEdit ? 'PUT' : 'POST';

      response = await fetch(URL, {
        method: method,
        headers: {
          'Content-type': 'application/json; charset=UTF-8',
          'Authorization': `Bearer ${token}` // ¡AÑADIR TOKEN!
        },
        body: JSON.stringify(permissionData)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onPermissionSaved) onPermissionSaved();
        resetForm();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando permiso: ", error);
      setError(error.message || "Ocurrió un error inesperado");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    permissionData,
    error,
    isLoading,
    handleChange,
    handleSubmit
  };
}
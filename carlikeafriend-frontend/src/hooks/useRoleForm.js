import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';

export const useRoleForm = (roleToEdit, onRoleSaved) => {
  const [roleData, setRoleData] = useState({
    name: '',
    description: '',
    permissions: []
  });

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const [allPermissions, setAllPermissions] = useState([]);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Manejar la carga de permisos manualmente
  const [isLoadingPermission, setIsLoadingPermission] = useState(true);
  const [permissionError, setPermissionError] = useState(null);
  const URL = API_CONFIG.PERMISSIONS;

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  useEffect(() => {
    const fetchPermissions = async () => {
      setIsLoadingPermission(true);
      setPermissionError(null);

      try {

        const response = await fetch(URL, {
          method: 'GET',
          headers: { 'Authorization': `Bearer ${token}` } // ¡AÑADIR TOKEN!
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
        setPermissionError(error.message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingPermission(false);
      }
    };

    if (token) fetchPermissions();

  }, [token, navigate, logout]);

  useEffect(() => {
    if (roleToEdit) {
      const permissionsIds = roleToEdit.permissions.map(permission => permission.id);
      setRoleData({
        name: roleToEdit.name,
        description: roleToEdit.description,
        permissions: permissionsIds
      });
    }
  }, [roleToEdit]);

  // Maneja cambios en los campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    setRoleData(prevData => ({ ...prevData, [name]: value }));
  };

  //Maneja cambios en los checklists
  const handleCheckListChange = (e) => {
    const { value, checked, name } = e.target;
    const selectedPermissionId = parseInt(value);

    setRoleData(prevData => {
      const currentList = prevData[name] || [];

      if (checked) {
        // Si el checkbox está marcado, agrega el valor a la lista
        return {
          ...prevData,
          [name]: [...currentList, selectedPermissionId]
        };
      } else {
        // Si no está marcada, filtra el objeto de permiso por su id.
        return {
          ...prevData,
          [name]: currentList.filter(item => item !== selectedPermissionId)
        };
      }
    });
  };

  // Función para resetear el formulario.
  const resetForm = useCallback(() => {
    setRoleData({
      name: '',
      description: '',
      permissions: []
    });
  }, []);

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    // Verifica si el array de permisos está vacío
    if (roleData.permissions.length === 0) {
      setError("Debes seleccionar al menos un permiso.");
      setIsLoading(false);
      return;
    }

    try {
      let response;
      const URL = roleToEdit
        ? `${API_CONFIG.ROLES}/${roleToEdit.id}`
        : API_CONFIG.ROLES;

      const method = roleToEdit ? 'PUT' : 'POST';

      response = await fetch(URL, {
        method: method,
        headers: {
          'Content-type': 'application/json; charset=UTF-8',
          'Authorization': `Bearer ${token}` // ¡AÑADIR TOKEN!
        },
        body: JSON.stringify(roleData)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onRoleSaved) onRoleSaved();
        resetForm();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando rol:", error);
      setError(error.message || "Ocurrió un error inesperado");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    roleData,
    allPermissions,
    isLoadingPermission,
    permissionError,
    error,
    isLoading,
    handleChange,
    handleCheckListChange,
    handleSubmit
  };
}
import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';

export const useUserForm = (userToEdit, onUserSaved) => {
  const [userData, setUserData] = useState({
    name: '',
    lastName: '',
    email: '',
    roles: []
  });

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const [allRoles, setAllRoles] = useState([]);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Cargar roles manualmente 
  const [isLoadingRole, setIsLoadingRole] = useState(true);
  const [roleError, setRoleError] = useState(null);
  const URL = API_CONFIG.ROLES;

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  useEffect(() => {
    const fetchRoles = async () => {
      setIsLoadingRole(true);
      setRoleError(null);

      try {

        const response = await fetch(URL, {
          method: 'GET',
          headers: { 'Authorization': `Bearer ${token}` } // ¡AÑADIR TOKEN!
        });

        if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

        if (response.ok) {
          const data = await response.json();
          setAllRoles(data);
        } else {
          // Manejo de otros errores (400, 500, etc.)
          const msg = await extractErrorMessage(response);
          throw new Error(msg);
        }
      } catch (error) {
        console.error("Error cargando datos del formulario: ", error);
        setRoleError(error.message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingRole(false);
      }
    };

    if (token) {
      fetchRoles();
    }

  }, [token, navigate, logout]);

  useEffect(() => {
    if (userToEdit) {
      const rolesIds = userToEdit.roles.map(role => role.id);
      setUserData({
        name: userToEdit.name,
        lastName: userToEdit.lastName,
        email: userToEdit.email,
        roles: rolesIds
      });
    }
  }, [userToEdit]);

  // Maneja cambios en los campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData(prevData => ({ ...prevData, [name]: value }));
  };

  //Maneja cambios en los checklists
  const handleCheckListChange = (e) => {
    const { value, checked, name } = e.target;
    const selectedRoleId = parseInt(value);

    setUserData(prevData => {
      const currentList = prevData[name] || [];

      if (checked) {
        // Si el checkbox está marcado, agrega el valor a la lista
        return {
          ...prevData,
          [name]: [...currentList, selectedRoleId]
        };
      } else {
        // Si no está marcada, filtra el objeto de rol por su id.
        return {
          ...prevData,
          [name]: currentList.filter(item => item !== selectedRoleId)
        };
      }
    });
  };

  // Función para resetear el formulario.
  const resetForm = useCallback(() => {
    setUserData({
      name: '',
      lastName: '',
      email: '',
      roles: []
    });
  }, []);

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    // Verifica si el array de roles está vacío
    if (userData.roles.length === 0) {
      setError("Debes seleccionar al menos un rol.");
      setIsLoading(false);
      return;
    }

    try {
      let response;
      const URL = userToEdit
        ? `${API_CONFIG.USERS}/${userToEdit.id}`
        : API_CONFIG.USERS;

      // Este formulario es solo para EDITAR (PUT), ya que el registro es público
      const method = 'PUT';
      if (!userToEdit) {
        throw new Error("Este formulario solo permite actualizar usuarios. El registro es público.");
      }

      response = await fetch(URL, {
        method: method,
        headers: {
          'Content-type': 'application/json; charset=UTF-8',
          'Authorization': `Bearer ${token}` // ¡AÑADIR TOKEN!
        },
        body: JSON.stringify(userData)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onUserSaved) onUserSaved();
        resetForm();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando usuario:", error);
      setError(error.message || "Ocurrió un error inesperado");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    userData,
    allRoles,
    isLoadingRole,
    roleError,
    error,
    isLoading,
    handleChange,
    handleCheckListChange,
    handleSubmit
  };
}
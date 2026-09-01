import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';
import { formatToLocalDateString } from '../utils/dateHelpers';

export const useUserForm = (userToEdit, onUserSaved) => {

  const { token, user, login, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const navigate = useNavigate(); 

  const [allRoles, setAllRoles] = useState([]);
  const [documentTypes, setDocumentTypes] = useState([]);
  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);

  // Cargar roles manualmente 
  const [isLoadingRole, setIsLoadingRole] = useState(true);
  const [roleError, setRoleError] = useState(null);

  // Cargar tipos de documento manualmente 
  const [isLoadingDocType, setIsLoadingDocType] = useState(true);
  const [docTypeError, setDocTypeError] = useState(null);


  useEffect(() => {
    const fetchInitialData = async () => {
      setIsLoadingRole(true);
      setIsLoadingDocType(true);
      setRoleError(null);
      setDocTypeError(null);

      try {

        const [roleRes, docTypeRes] = await Promise.all([
          fetch(API_CONFIG.ROLES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(API_CONFIG.DOCUMENT_TYPES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        if (handleUnauthorizedError(roleRes, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(docTypeRes, navigate, logout, setModalMessage)) return; // Detener si fue un 401

        // Procesar Roles
        if (roleRes.ok) {
          const roleData = await roleRes.json();
          setAllRoles(roleData);
        } else {
          // Manejo de otros errores (400, 500, etc.)
          const roleMsg = await extractErrorMessage(roleRes);
          setRoleError(roleMsg);
        }

        // Procesar Tipo de Documento
        if (docTypeRes.ok) {
          const docTypeData = await docTypeRes.json();
          setDocumentTypes(docTypeData);
        } else {
          // Manejo de otros errores (400, 500, etc.)
          const docTypeMsg = await extractErrorMessage(docTypeRes);
          setDocTypeError(docTypeMsg);
        }

      } catch (error) {
        console.error("Error de red en carga inicial:", error);
        // Errores de conexión (cuando no hay respuesta del servidor)
        const networkMsg = "No se pudo establecer conexión con el servidor.";
        setRoleError(networkMsg);
        setDocTypeError(networkMsg);
      } finally {
        setIsLoadingRole(false);
        setIsLoadingDocType(false);
      }
    };

    if (token) {
      fetchInitialData();
    }

  }, [token, navigate, logout]);

  // Recibe la data validada por react-hook-form
  const submitUserData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);

    const payload = {
      ...data,
      // Convertimos el array de strings nuevamente a un array de números para el backend
      roleIds: data.roleIds.map(id => Number(id)),
      // Ajustamos las fechas a string formato 'YYYY-MM-DD' si vienen como objetos Date
      birthDate: formatToLocalDateString(data.birthDate),
      driverLicenseExpiry: formatToLocalDateString(data.driverLicenseExpiry)
    };

    try {

      const method = 'PUT';
      if (!userToEdit) throw new Error("Este formulario solo permite actualizar usuarios.");

      const response = await fetch(`${API_CONFIG.USERS}/${userToEdit.id}`, {
        method: method,
        headers: {
          'Content-type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

      if (response.ok) {
        const userData = await response.json();
        // 1. VALIDACIÓN CLAVE: ¿El usuario que acabo de editar soy yo mismo?
        if (userToEdit.id === user.id) {

          // VALIDACIÓN ESTRICTA: Si no hay token nuevo, hay un riesgo de seguridad.
          if (!userData.token) {
            console.error("Alerta de seguridad: El backend no devolvió un nuevo token tras cambiar los roles.");
            logout(); // Expulsamos al usuario por seguridad
            return;
          }

          // 2. Armamos el objeto con la estructura que espera la función login
          const updatedSessionData = {
            token: userData.token,
            id: userData.id,
            name: userData.name,
            lastName: userData.lastName,
            userName: userData.userName,
            roles: userData.roles, // ¡Aquí vienen los nuevos roles
          };

          // 3. Sobreescribimos la sesión. Esto actualiza el estado global y el localStorage
          login(updatedSessionData);
        }

        if (onUserSaved) onUserSaved();

      } else {
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando usuario:", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    allRoles,
    documentTypes,
    isLoadingRole,
    isLoadingDocType,
    roleError,
    docTypeError,
    error,
    isSubmittingForm,
    submitUserData
  };
}
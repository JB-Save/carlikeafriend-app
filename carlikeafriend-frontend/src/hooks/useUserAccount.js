import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';
import { formatToLocalDateString } from '../utils/dateHelpers';

export const useUserAccount = () => {

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const navigate = useNavigate(); 
  const [userAccountData, setUserAccountData] = useState(null);
  const [documentTypes, setDocumentTypes] = useState([]);

  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);
  const [toastType, setToastType] = useState("success");
  const [isLoading, setIsLoading] = useState(false);


  useEffect(() => {
    const fetchUserAccountData = async () => {
      setIsLoading(true);
      setToastMessage(null);

      try {

        const [userDataRes, docTypeRes] = await Promise.all([
          fetch(API_CONFIG.USER_ACCOUNT, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(API_CONFIG.DOCUMENT_TYPES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        if (handleUnauthorizedError(userDataRes, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(docTypeRes, navigate, logout, setModalMessage)) return; // Detener si fue un 401

        if (!userDataRes.ok || !docTypeRes.ok) {
          const errorResponse = !userDataRes.ok ? userDataRes.ok : docTypeRes;
          const msg = await extractErrorMessage(errorResponse);
          throw new Error(msg);
        }

        const [userAccData, DocTypeData] = await Promise.all([
          userDataRes.json(),
          docTypeRes.json()
        ]);

        setUserAccountData(userAccData);
        setDocumentTypes(DocTypeData);

      } catch (error) {
        console.error("Error en carga inicial de datos del formulario: ", error);
        const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
        setToastMessage(message || "Ocurrió un error inesperado.");
        setToastType("failure");
        setShowToast(true);
      } finally {
        setIsLoading(false);
      }
    };

    if (token) {
      fetchUserAccountData();
    }

  }, [token, navigate, logout]);

  const submitUserData = async (data) => {
    setIsLoading(true);
    setShowToast(null);

    try {

      const payload = {
        ...data,
        // Ajustamos las fechas a string formato 'YYYY-MM-DD' si vienen como objetos Date
        birthDate: formatToLocalDateString(data.birthDate),
        driverLicenseExpiry: formatToLocalDateString(data.driverLicenseExpiry)
      };

      const response = await fetch(API_CONFIG.USER_ACCOUNT, {
        method: 'PUT',
        headers: {
          'Content-type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        setToastMessage("¡Perfil actualizado con éxito!");
        setToastType("success");
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error al actualizar el perfil: ", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setToastMessage(message || "Hubo un problema al guardar los cambios. Inténtalo de nuevo.");
      setToastType("failure");
    } finally {
      setIsLoading(false);
      setShowToast(true);
    }
  };

  return {
    userAccountData,
    documentTypes,
    showToast,
    toastMessage,
    toastType,
    isLoading,
    setShowToast,
    submitUserData
  };
}
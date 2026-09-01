import { useState, useCallback, useContext, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const useSignInForm = () => {

  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(null);

  const navigate = useNavigate();
  const location = useLocation(); // Obtenemos la ubicación actual y su estado
  const { login } = useContext(UserContext); // Obtener la función 'login' del contexto

  const submitSignInData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);

    const payload = {
      ...data
    };

    try {

      const response = await fetch(API_CONFIG.LOGIN, {
        method: 'POST',
        headers: {
          'Content-type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      // Si el inicio de sesión es exitoso
      if (response.ok) {
        const userAuthData = await response.json();

        //Llamar a la función login del contexto para guardar el usuario
        login(userAuthData); // Guardar los datos del usuario, incluyendo el rol

        //Redireccionar al usuario logueado
        const origin = location.state?.from?.pathname || "/";
        navigate(origin, { replace: true });
      } else {
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error en login: ", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado.");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    error,
    isSubmittingForm,
    submitSignInData
  };
}
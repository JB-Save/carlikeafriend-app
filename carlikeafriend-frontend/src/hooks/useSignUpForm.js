import { useState, useCallback } from 'react';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const useSignUpForm = (onUserSignedUp) => {

  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(null);

  const submitSignUpData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);

    const payload = {
      ...data
    };

    try {

      const method = 'POST';

      const response = await fetch(API_CONFIG.REGISTER, {
        method: method,
        headers: {
          'Content-type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const dataResult = await response.json();
        if (onUserSignedUp) onUserSignedUp(dataResult.email);
      } else {
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error en signup: ", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado.");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    error,
    isSubmittingForm,
    submitSignUpData
  };
}
import { useState, useCallback } from 'react';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const useSignUpForm = (onUserSignedUp) => {
  const [initialForm, setInitialForm] = useState({
    name: '',
    lastName: '',
    email: '',
    password: ''
  });
  const [newUserData, setNewUserData] = useState(initialForm);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(null);

  // Expresión regular para validar el formato básico del email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Expresión regular para una contraseña: Mínimo 8 caracteres, al menos una letra mayúscula, una letra minúscula, un número y un carácter especial
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  // Función de validación del formulario
  const validateForm = (data) => {
    // Validación de Nombre y Apellido: no vacíos y longitud mínima
    if (data.name.trim().length < 3) {
      return "El nombre debe tener entre 3 y 100 caracteres caracteres.";
    }
    if (data.lastName.trim().length < 3) {
      return "El apellido debe tener entre 3 y 100 caracteres caracteres.";
    }

    // Validación de Email: formato correcto
    if (!emailRegex.test(data.email)) {
      return "Por favor, introduce una dirección de correo electrónico válida (ej: name@domain.com).";
    }

    // Validación de Contraseña: complejidad
    if (!passwordRegex.test(data.password)) {
      return "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.";
    }

    // Si todas las validaciones pasan
    return null;
  }

  // Maneja cambios en los campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewUserData(prevData => ({ ...prevData, [name]: value }));
    if (error) {
      setError(null);
    }
  };


  // Función para resetear el formulario.
  const resetForm = useCallback(() => {
    setNewUserData({
      name: '',
      lastName: '',
      email: '',
      password: ''
    });
  }, []);

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    // Validación del lado del cliente
    const validationError = validateForm(newUserData);
    if (validationError) {
      setError(validationError);
      setIsLoading(false);
      return; // Detiene la ejecución si hay errores de validación
    }


    try {
      let response;
      const URL = API_CONFIG.REGISTER;

      const method = 'POST';

      response = await fetch(URL, {
        method: method,
        headers: {
          'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(newUserData)
      });

      if (response.ok) {
        const dataResult = await response.json();
        if (onUserSignedUp) onUserSignedUp(dataResult.email);
        resetForm();
      } else {
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error en signup: ", error);
      setError(error.message || "Ocurrió un error inesperado.");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    newUserData,
    error,
    isLoading,
    handleChange,
    handleSubmit
  };
}
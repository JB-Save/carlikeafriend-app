import { useState, useCallback, useContext, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const useSignInForm = () => {
  const [initialForm, setInitialForm] = useState({
    email: '',
    password: ''
  });
  const [userData, setUserData] = useState(initialForm);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(null);

  const navigate = useNavigate();
  const location = useLocation(); // Obtenemos la ubicación actual y su estado
  const { login } = useContext(UserContext); // Obtener la función 'login' del contexto

  // Expresión regular para validar el formato básico del email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Expresión regular para una contraseña: Mínimo 8 caracteres, al menos una letra mayúscula, una letra minúscula, un número y un carácter especial
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  // Función de validación del formulario
  const validateForm = (data) => {
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
    setUserData(prevData => ({ ...prevData, [name]: value }));
    if (error) {
      setError(null);
    }
  };


  // Función para resetear el formulario.
  const resetForm = useCallback(() => {
    setUserData({
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
    const validationError = validateForm(userData);
    if (validationError) {
      setError(validationError);
      setIsLoading(false);
      return; // Detiene la ejecución si hay errores de validación
    }

    try {
      let response;
      const URL = API_CONFIG.LOGIN;

      response = await fetch(URL, {
        method: 'POST',
        headers: {
          'Content-type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(userData)
      });

      // Si el inicio de sesión es exitoso
      if (response.ok) {
        const userAuthData = await response.json(); // Asume que el backend devuelve { token: '...', user: { id: 1, name: '...', role: 'ADMIN' } }

        //Llamar a la función login del contexto para guardar el usuario
        login(userAuthData); // Guardar los datos del usuario, incluyendo el rol

        resetForm();

        //Redireccionar al usuario logueado
        const origin = location.state?.from?.pathname || "/";
        navigate(origin, { replace: true });
      } else {
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error en login: ", error);
      setError(error.message || "Ocurrió un error inesperado.");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    userData,
    error,
    isLoading,
    handleChange,
    handleSubmit
  };
}
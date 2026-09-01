import { useCallback, useEffect, useMemo, useState } from "react"
import { UserContext } from "./UserContext"
import { API_CONFIG } from "../config/apiConfig";


export const UserProvider = ({ children }) => {
    //Estado para el usuario y para el proceso de carga inicial
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const URL = API_CONFIG.AUTH;
    /*
      Función para iniciar sesión.
      Guarda los datos del usuario (incluyendo el token/rol) en el estado y en localStorage.
     */
    const login = useCallback((userData) => {
        // userData debe contener el rol, el nombre, el token, etc., que regresa el backend.
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    }, []);

    /*
     Función para cerrar sesión.
     Limpia el estado del usuario y elimina los datos de localStorage.
     */
    const logout = useCallback(() => {
        setUser(null);
        localStorage.removeItem('user');
    }, []);

    //Cerrar sesión en otras pestañas del navegador
    useEffect(() => {
        const syncLogout = (event) => {
            // Si otra pestaña eliminó la llave 'user', esta pestaña también cierra sesión
            if (event.key === 'user' && !event.newValue) {
                logout();
            }
        };

        window.addEventListener('storage', syncLogout);

        return () => {
            window.removeEventListener('storage', syncLogout);
        };
    }, [logout]);

    // Efecto para inicializar y verificar el estado de la sesión
    useEffect(() => {
        const checkSession = async () => {
            const storedUser = localStorage.getItem('user');

            if (storedUser) {
                try {
                    const userData = JSON.parse(storedUser);

                    // Llamar al backend para validar el token
                    // Esto evita que un usuario manipule el localStorage con un token caducado o falso.
                    const response = await fetch(URL, {
                        method: 'GET',
                        headers: { 'Authorization': `Bearer ${userData.token}` }
                    });

                    if (response.ok) {
                        // Si el token es válido, el backend devuelve los datos frescos del usuario
                        const freshUserData = await response.json();

                        // Combinamos el token guardado con los datos frescos del usuario
                        const updatedSessionData = {
                            token: userData.token, // Mantenemos el token
                            id: freshUserData.id,
                            name: freshUserData.name,
                            lastName: freshUserData.lastName,
                            userName: freshUserData.userName,
                            roles: freshUserData.roles,
                        };

                        // Usamos la función 'login' para guardar el estado y el localStorage
                        login(updatedSessionData);
                    } else {
                        // Si la respuesta es 401 o cualquier error, el token es inválido.
                        logout();
                    }

                } catch (error) {
                    console.error("Error al verificar la sesión:", error);
                    logout(); // Desloguear si hay cualquier error
                }
            }
            setIsLoading(false); // La verificación ha terminado

        };
        checkSession();
    }, [login, logout]);

    const contextValue = useMemo(() => ({
        user,
        isAuthenticated: !!user,
        token: user?.token,
        login,
        logout
    }), [user, login, logout]);

    // Renderizado Condicional
    // Evita renderizar el resto de la app hasta que se conozca el estado de autenticación.
    if (isLoading) {
        // Muestra una pantalla de carga global
        return (
            <div className="text-center my-5" style={{ paddingTop: '10rem' }}>
                <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                    <span className="visually-hidden">Cargando...</span>
                </div>
                <p className="mt-3 text-muted">Verificando sesión...</p>
            </div>
        );
    }

    return (
        <UserContext.Provider value={contextValue}>
            {children}
        </UserContext.Provider>
    )
}
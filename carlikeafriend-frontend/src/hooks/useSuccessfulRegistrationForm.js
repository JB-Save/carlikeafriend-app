import { useEffect, useState } from "react";
import { extractErrorMessage } from "../utils/extractErrorMessage";
import { API_CONFIG } from "../config/apiConfig";

// Tiempo de espera en segundos
const COOLDOWN_SECONDS = 30; // 30 segundos de espera entre reenvíos

export const useSuccessfulRegistrationForm = () => {

    const [status, setStatus] = useState('registered'); // 'registered', 'resend_loading', 'resend_success', 'resend_error'
    const [errorMessage, setErrorMessage] = useState('');
    const [cooldown, setCooldown] = useState(0);

    // La URL de tu backend
    const RESEND_ENDPOINT = API_CONFIG.EMAIL_RESEND_CONFIRMATION;

    // Hook para manejar el temporizador de Cooldown
    useEffect(() => {
        let timer;
        if (cooldown > 0) {
            // Si el cooldown es > 0, iniciamos el temporizador
            timer = setInterval(() => {
                setCooldown(prevCooldown => {
                    if (prevCooldown <= 1) {
                        // Cuando el temporizador termina, lo limpiamos y reiniciamos el estado
                        clearInterval(timer);
                        setStatus('registered'); // Vuelve al estado base para permitir un nuevo clic
                        return 0;
                    }
                    return prevCooldown - 1;
                });// Decremento cada segundo
            }, 1000)
        }
        // Función de limpieza para detener el temporizador si el componente se desmonta o el cooldown cambia
        return () => {
            if (timer) {
                clearInterval(timer);
            }
        };
    }, [cooldown]); // Se re-ejecuta cuando el cooldown cambia

    const handleResend = async (email) => {
        // Bloquea si el cooldown está activo
        if (cooldown > 0) return;

        setStatus('resend_loading');
        setErrorMessage('');

        try {
            const response = await fetch(RESEND_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email }),
            });

            if (response.ok) {
                // Si llegamos aquí, la respuesta fue exitosa (200 OK)
                // Inicia el cooldown al enviar exitosamente
                setCooldown(COOLDOWN_SECONDS);
                // Mostramos un estado de éxito
                setStatus('resend_success');
                console.info("Backend Success Message");
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error reenviando correo: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            // Ahora el mensage de error contendrá la razón real del fallo (ej: "Email no encontrado" o "Límite excedido")
            setErrorMessage(message || "Ocurrió un error inesperado.");
            setStatus('resend_error');
        }
    };

    return {
        status,
        errorMessage,
        cooldown,
        handleResend
    }


}

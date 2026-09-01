import { useLocation } from "react-router-dom";
import { useSuccessfulRegistrationForm } from "../hooks/useSuccessfulRegistrationForm";
import { useEffect, useState } from "react";

export const RegistrationSuccessAndResend = () => {

    const {
        status,
        errorMessage,
        cooldown,
        handleResend } = useSuccessfulRegistrationForm();

    const [email, setEmail] = useState(null);
    const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
    // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
    useEffect(() => {
        // Accede a los datos del estado de la navegación
        if (location.state && location.state.emailUser) { // Verificamos si existe el estado de navegación y si contiene la propiedad emailUser
            setEmail(location.state.emailUser); // actualizamos el estado local con el email del usuario
        } else {
            setEmail(null); // Limpia el estado si no hay datos del email del usuario
        }
    }, [location]); // Vuelve a ejecutar cuando 'location' cambie

    const renderResendButton = () => {
        // Verifica si el cooldown está activo o si está cargando
        const isDisabled = cooldown > 0 || status === 'resend_loading';
    
        // Muestra el tiempo restante o el estado actual del botón
        const buttonText = cooldown > 0
            ? `Espere ${cooldown}s para reenviar` // Muestra el tiempo restante
            : (status === 'resend_loading' ? 'Enviando...' : 'Reenviar Correo de Confirmación');

        // Renderiza el botón solo si el estado no es 'resend_success' O si el cooldown está activo.
        if (status !== 'resend_success' || cooldown > 0) {

            return (
                <button
                    onClick={() => email && handleResend(email)}
                    // Se actualizan las clases para manejar el estado deshabilitado visualmente
                    className={`w-100 mt-4 d-flex align-items-center justify-content-center fw-bold py-2 px-4 rounded-3 ${isDisabled ? 'btn-secondary text-muted opacity-75 cursor-not-allowed' : 'header-btn'}`}
                    disabled={isDisabled}
                >
                    {status === 'resend_loading' && (
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    )}
                    {buttonText}
                </button>
            );
        }
        // Si el estado es 'resend_success' Y el cooldown ya terminó (cooldown === 0), no renderizamos el botón aquí.
        return null;

    };

    // Muestra el mensaje de éxito del reenvío
    const renderSuccessMessage = () => {
        if (status === 'resend_success') {
            return (
                <div className="mt-4 alert alert-success d-flex align-items-center" role="alert">
                    <svg className="bi flex-shrink-0 me-2" width="24" height="24" role="img" aria-label="Success:">
                        <use xlinkHref="#check-circle-fill" />
                    </svg>
                    <div>
                        Reenviado. Si la cuenta está registrada, recibirás un correo en <span className="fw-bold">{email}</span> en breve.
                    </div>
                </div>
            );
        }
        return null;
    }

    // Muestra un mensaje de error si hubo un problema de reenvío
    const renderErrorMessage = () => {
        if (status === 'resend_error' && errorMessage) {
            return (
                <div className="alert alert-danger mt-3" role="alert">
                    <strong>Error:</strong> {errorMessage}
                </div>
            );
        }
        return null;
    };

    if (!email) {
        return <div className="text-center mt-5">Cargando datos de usuario o usuario no encontrado...</div>;
    }

    return (
        <div col="row">
            <div className="min-vh-100 d-flex align-items-center justifiy-content-center">
                <div className="container">
                    <div className="col-12 col-md-6 mx-auto">
                        <div className="card card-shadow card-background p-4 mx-4">
                            <div className="text-center mb-3">
                                <svg style={{ width: '48px', height: '48px', color: '#10b981' }} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
                                </svg>
                                <h2 className="mt-2 fs-3 fw-bolder text-dark">
                                    ¡Registro Exitoso!
                                </h2>
                                <p className="mt-2 text-muted fs-6">
                                    Debes confirmar tu cuenta. Hemos enviado un correo a:
                                </p>
                                <p className="fs-5 fw-bold mt-1 text-primary">{email}</p>
                            </div>

                            <div className="d-grid gap-3">
                                <p className="text-muted text-justify">
                                    Por favor, revisa tu bandeja de entrada y sigue el enlace para activar tu cuenta. (Asegúrate de revisar tu carpeta de **Spam** o **Correo No Deseado**).
                                </p>

                                <div className="pt-3 border-top border-secondary-subtle">
                                    <p className="fw-semibold text-primary">
                                        ¿No has recibido el correo?
                                    </p>
                                    {/* Muestra el mensaje de éxito (alerta verde) si el reenvío fue OK */}
                                    {renderSuccessMessage()}

                                    {/* Muestra el botón (o el temporizador/loading) */}
                                    {renderResendButton()}

                                    {/* Muestra el mensaje de error si el reenvío falló */}
                                    {renderErrorMessage()}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

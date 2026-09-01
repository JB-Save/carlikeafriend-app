import React, { useContext, useState } from 'react';
import { ToastNotification } from './ToastNotification';
import { UserContext } from '../context/UserContext';
import { useMessageModal } from '../context/MessageModalContext';
import { useNavigate } from 'react-router-dom';
import { API_CONFIG } from '../config/apiConfig';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { changePasswordSchema } from '../utils/validationSchema';

export const SecuritySettingsComponent = () => {
    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState(null);
    const [toastType, setToastType] = useState("success");
    const [isLoading, setIsLoading] = useState(false);

    // Estados para el modal de desactivación de la cuenta
    const [showDeactivateModal, setShowDeactivateModal] = useState(false);
    const [isDeactivating, setIsDeactivating] = useState(false);


    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(changePasswordSchema),
        defaultValues: {
            currentPassword: '',
            newPassword: '',
            confirmPassword: ''
        },
        mode: 'onTouched' // Valida cuando el usuario sale del input
    });

    const handlePasswordSubmit = async (data) => {
        setIsLoading(true);
        setToastMessage(null);

        const payload = {
            currentPassword: data.currentPassword,
            newPassword: data.newPassword
        };

        try {
            const response = await fetch(API_CONFIG.CHANGE_PASSWORD, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                const data = await response.json();
                setToastMessage(data.message || "Contraseña modificada correctamente.");
                setToastType("success");
                setShowToast(true);

                // Limpiar los campos tras el éxito
                reset();

            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al actualizar la contraseña: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setToastMessage(message || "Ocurrió un error inesperado.");
            setToastType("failure");
            setShowToast(true);
        } finally {
            setIsLoading(false);
        }
    };

    const handleDeactivateAccount = async () => {
        setIsDeactivating(true);

        try {

            const response = await fetch(`${API_CONFIG.USER_ACCOUNT}/me/deactivate`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                const data = await response.json();
                setModalMessage(data.message || "Tu cuenta ha sido desactivada exitosamente.");

                // Cerrar modal y limpiar contexto
                setShowDeactivateModal(false);
                logout();

                // Redirigir al usuario
                navigate('/login');

            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.log('No se pudo desactivar la cuenta: ' + error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setToastMessage(message || "No se pudo desactivar la cuenta");
            setToastType("failure");
            setShowToast(true);
            setShowDeactivateModal(false);
        } finally {
            setIsDeactivating(false);
        }
    };


    return (
        <div className="w-100">
            <div className="border-bottom pb-2 mb-4">
                <h3 className="h4 fw-bold my-security-block-title-color mb-0">
                    <i className="bi bi-shield-lock-fill me-2 text-secondary"></i> Seguridad de la Cuenta
                </h3>
                <p className="my-security-block-text-muted small mb-0">Gestiona tus credenciales de acceso y mantén tu cuenta segura.</p>
            </div>

            <div className="row g-4">
                <div className="col-lg-7">
                    <form onSubmit={handleSubmit(handlePasswordSubmit)} className="card p-3 border rounded-3 bg-light-subtle">
                        <h5 className="h6 fw-bold mb-3 my-security-block-title-color">Cambiar Contraseña</h5>

                        <div className="mb-3">
                            <label htmlFor="currentPassword" className="form-label small fw-semibold my-security-block-text-primary">Contraseña Actual</label>
                            <input
                                id="currentPassword"
                                type="password"
                                className={`form-control rounded-3 ${errors.currentPassword ? 'is-invalid' : ''}`}
                                {...register('currentPassword')}
                                disabled={isLoading}
                            />
                            {errors.currentPassword && <div className="invalid-feedback">{errors.currentPassword.message}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="newPassword" className="form-label small fw-semibold my-security-block-text-primary">Nueva Contraseña</label>
                            <input
                                id="newPassword"
                                type="password"
                                className={`form-control rounded-3 ${errors.newPassword ? 'is-invalid' : ''}`}
                                {...register('newPassword')}
                                disabled={isLoading}
                            />
                            {errors.newPassword && <div className="invalid-feedback">{errors.newPassword.message}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="confirmPassword" className="form-label small fw-semibold my-security-block-text-primary">Confirmar Nueva Contraseña</label>
                            <input
                                id="confirmPassword"
                                type="password"
                                className={`form-control rounded-3 ${errors.confirmPassword ? 'is-invalid' : ''}`}
                                {...register('confirmPassword')}
                                disabled={isLoading}
                            />
                            {errors.confirmPassword && <div className="invalid-feedback">{errors.confirmPassword.message}</div>}
                        </div>

                        <button type="submit" className="btn form-btn w-100 rounded-3 mt-2 shadow-sm" disabled={isLoading}>
                            {isLoading ? 'Actualizando...' : 'Actualizar Contraseña'}
                        </button>
                    </form>
                </div>

                <div className="col-lg-5">
                    <div className="card p-3 border border-warning-subtle rounded-3 h-100" style={{ backgroundColor: '#fffdf5' }}>
                        <h5 className="h6 fw-bold text-warning-dominant mb-2">
                            <i className="bi bi-exclamation-octagon-fill me-2 text-warning"></i> Zona de Cuidado
                        </h5>
                        <p className="form-text my-security-block-text-muted small">
                            Al deshabilitar o solicitar la baja de tu cuenta de usuario, perderás acceso inmediato a tus paneles e historiales de facturación en curso.
                        </p>
                        <button
                            onClick={() => setShowDeactivateModal(true)}
                            className="btn btn-outline-danger btn-sm rounded-3 mt-auto w-100 align-self-end">
                            Desactivar mi cuenta
                        </button>
                    </div>
                </div>
            </div>

            {/* Modal de Confirmación */}
            <DeleteConfirmationModalComponent
                show={showDeactivateModal}
                isDeleting={isDeactivating}
                deleteFunction={handleDeactivateAccount}
                onClose={() => setShowDeactivateModal(false)}
                customMessage="¿Estás completamente seguro de que deseas desactivar tu cuenta? Esta acción cancelará tus accesos inmediatamente."
                customButtonText="Desactivar Cuenta"
            />

            <ToastNotification show={showToast} message={toastMessage} type={toastType} onClose={() => setShowToast(false)} />
        </div>
    );
};
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { usePolicyTypeForm } from '../hooks/usePolicyTypeForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { singleFieldSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const PolicyTypeForm = ({ policyTypeToEdit, onPolicyTypeSaved }) => {

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const {
        error: apiError,
        isSubmittingForm: isLoading,
        submitPolicyTypeData
    } = usePolicyTypeForm(policyTypeToEdit, onPolicyTypeSaved);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(singleFieldSchema)
    });

    useEffect(() => {
        if (policyTypeToEdit) {
            reset({
                name: policyTypeToEdit.name
            });
        }
    }, [policyTypeToEdit, reset]);

    return (
        <form onSubmit={handleSubmit(submitPolicyTypeData)} >
            <div className="mb-3">
                <label htmlFor="name" className="form-label fw-bold">Nombre</label>
                <input
                    type="text"
                    id="name"
                    className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                    {...register('name')}
                    disabled={isLoading}
                />
                {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
            </div>

            {apiError && (
                <div className="alert alert-danger shadow-sm" role="alert">
                    <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
                </div>
            )}
            <div className="d-flex justify-content-between mt-5 pt-3 border-top">
                <button
                    type="button"
                    className="btn form-btn rounded-3 px-4"
                    onClick={() => navigate("/administration/policyType-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : policyTypeToEdit ? 'Actualizar Tipo' : 'Registrar Tipo'}
                </button>
            </div>
        </form>
    );
}


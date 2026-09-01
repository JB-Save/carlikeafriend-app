import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { usePolicyForm } from '../hooks/usePolicyForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { policySchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const PolicyForm = ({ policyToEdit, onPolicySaved }) => {
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const {
        allPolicyTypes,
        isLoadingPolicyType,
        policyTypeError,
        error: apiError,
        isSubmittingForm,
        submitPolicyData
    } = usePolicyForm(policyToEdit, onPolicySaved);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(policySchema)
    });

    useEffect(() => {
        if (policyToEdit && !isLoadingPolicyType) {
            reset({
                name: policyToEdit.name,
                policyTypeId: policyToEdit.policyType.id.toString(),
                content: policyToEdit.content
            });
        }
    }, [policyToEdit, isLoadingPolicyType, reset]);

    const isLoading = isSubmittingForm || isLoadingPolicyType;

    return (
        <form onSubmit={handleSubmit(submitPolicyData)} >
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
            <div className="mb-3">
                <label htmlFor="policyTypeId" className="form-label fw-bold">Tipo de Política</label>
                {policyTypeError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{policyTypeError}</small>
                    </div>
                )}
                <select
                    id="policyTypeId"
                    className={`form-control ${errors.policyTypeId ? 'is-invalid' : ''}`}
                    {...register('policyTypeId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona un tipo de Política...
                    </option>
                    {allPolicyTypes.map((type) => (
                        <option key={type.id} value={type.id}>
                            {type.name}
                        </option>
                    ))}
                </select>
                {errors.policyTypeId && <div className="invalid-feedback">{errors.policyTypeId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="content" className="form-label fw-bold">Contenido</label>
                <textarea
                    id="content"
                    rows="3"
                    className={`form-control ${errors.content ? 'is-invalid' : ''}`}
                    {...register('content')}
                    disabled={isLoading}
                ></textarea>
                {errors.content && <div className="invalid-feedback">{errors.content.message}</div>}
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
                    onClick={() => navigate("/administration/policy-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : policyToEdit ? 'Actualizar Política' : 'Registrar Política'}
                </button>
            </div>
        </form>
    );
}


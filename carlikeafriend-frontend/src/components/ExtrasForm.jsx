import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useExtrasForm } from '../hooks/useExtrasForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { extrasSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const ExtrasForm = ({ extrasToEdit, onExtrasSaved }) => {

    const navigate = useNavigate(); 

    const {
        allChargeTypes,
        isLoadingChargeTypes,
        chargeTypeError,
        error: apiError,
        isSubmittingForm,
        submitExtrasData
    } = useExtrasForm(extrasToEdit, onExtrasSaved);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(extrasSchema)
    });

    useEffect(() => {
        if (extrasToEdit && !isLoadingChargeTypes) {
            reset({
                name: extrasToEdit.name,
                description: extrasToEdit.description,
                currentPrice: extrasToEdit.currentPrice,
                chargeType: extrasToEdit.chargeType,
                maxQuantityPerReservation: extrasToEdit.maxQuantityPerReservation,
                maxChargeableDays: extrasToEdit.maxChargeableDays
            });
        }
    }, [extrasToEdit, isLoadingChargeTypes, reset]);

    const isLoading = isSubmittingForm || isLoadingChargeTypes;

    return (
        <form onSubmit={handleSubmit(submitExtrasData)} >
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
                <label htmlFor="description" className="form-label fw-bold">Descripción</label>
                <textarea
                    id="description"
                    rows="3"
                    className={`form-control ${errors.description ? 'is-invalid' : ''}`}
                    {...register('description')}
                    disabled={isLoading}
                ></textarea>
                {errors.description && <div className="invalid-feedback">{errors.description.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="currentPrice" className="form-label fw-bold">Precio</label>
                <input
                    type="number"
                    id="currentPrice"
                    step="0.01"
                    className={`form-control ${errors.currentPrice ? 'is-invalid' : ''}`}
                    {...register('currentPrice')}
                    disabled={isLoading}
                />
                {errors.currentPrice && <div className="invalid-feedback">{errors.currentPrice.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="chargeType" className="form-label fw-bold">Tipo de Cargo</label>
                {chargeTypeError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{chargeTypeError}</small>
                    </div>
                )}
                <select
                    id="chargeType"
                    className={`form-select ${errors.chargeType ? 'is-invalid' : ''}`}
                    {...register('chargeType')}
                    disabled={isLoading}
                >
                    <option value="">
                        Seleccione un Tipo de Cargo...
                    </option>
                    {allChargeTypes.map((type) => (
                        <option key={type.value} value={type.value}>
                            {type.label}
                        </option>
                    ))}
                </select>
                {errors.chargeType && <div className="invalid-feedback">{errors.chargeType.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="maxQuantityPerReservation" className="form-label fw-bold">Cantidad máxima por reserva</label>
                <input
                    type="number"
                    id="maxQuantityPerReservation"
                    step="1"
                    className={`form-control ${errors.maxQuantityPerReservation ? 'is-invalid' : ''}`}
                    {...register('maxQuantityPerReservation')}
                    disabled={isLoading}
                />
                {errors.maxQuantityPerReservation && <div className="invalid-feedback">{errors.maxQuantityPerReservation.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="maxChargeableDays" className="form-label fw-bold">Días máximos facturables</label>
                <input
                    type="number"
                    id="maxChargeableDays"
                    step="1"
                    className={`form-control ${errors.maxChargeableDays ? 'is-invalid' : ''}`}
                    {...register('maxChargeableDays')}
                    disabled={isLoading}
                />
                {errors.maxChargeableDays && <div className="invalid-feedback">{errors.maxChargeableDays.message}</div>}
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
                    onClick={() => navigate("/administration/extras-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : extrasToEdit ? 'Actualizar Extra' : 'Registrar Extra'}
                </button>
            </div>
        </form>
    );
}


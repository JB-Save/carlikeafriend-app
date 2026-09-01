import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useBranchForm } from '../hooks/useBranchForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { branchSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const BranchForm = ({ branchToEdit, onBranchSaved }) => {
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const {
        allCities,
        isLoadingCity,
        cityError,
        error: apiError,
        isSubmittingForm,
        submitBranchData
    } = useBranchForm(branchToEdit, onBranchSaved);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(branchSchema)
    });

    useEffect(() => {
        if (branchToEdit && !isLoadingCity) {
            reset({
                name: branchToEdit.name,
                address: branchToEdit.address,
                cityId: branchToEdit.city.id.toString(),
                latitude: branchToEdit.latitude,
                longitude: branchToEdit.longitude
            });
        }
    }, [branchToEdit, isLoadingCity, reset]);

    const isLoading = isSubmittingForm || isLoadingCity;

    return (
        <form onSubmit={handleSubmit(submitBranchData)} >
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
                <label htmlFor="address" className="form-label fw-bold">Dirección</label>
                <input
                    type="text"
                    id="address"
                    className={`form-control ${errors.address ? 'is-invalid' : ''}`}
                    {...register('address')}
                    disabled={isLoading}
                />
                {errors.address && <div className="invalid-feedback">{errors.address.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="cityId" className="form-label fw-bold">Ciudad</label>
                {cityError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{cityError}</small>
                    </div>
                )}
                <select
                    id="cityId"
                    className={`form-select ${errors.cityId ? 'is-invalid' : ''}`}
                    {...register('cityId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona una Ciudad...
                    </option>
                    {allCities.map((city) => (
                        <option key={city.id} value={city.id}>
                            {city.name}
                        </option>
                    ))}
                </select>
                {errors.cityId && <div className="invalid-feedback">{errors.cityId.message}</div>}
            </div>
            <div className="row">
                <div className="col-md-6 mb-3">
                    <label htmlFor="latitude" className="form-label fw-bold">Latitud</label>
                    <input
                        type="number"
                        id="latitude"
                        step="0.00000001"
                        className={`form-control ${errors.latitude ? 'is-invalid' : ''}`}
                        {...register('latitude')}
                        disabled={isLoading}
                    />
                    {errors.latitude && <div className="invalid-feedback">{errors.latitude.message}</div>}
                </div>
                <div className="col-md-6 mb-3">
                    <label htmlFor="longitude" className="form-label fw-bold">Longitud</label>
                    <input
                        type="number"
                        id="longitude"
                        step="0.00000001"
                        className={`form-control ${errors.longitude ? 'is-invalid' : ''}`}
                        {...register('longitude')}
                        disabled={isLoading}
                    />
                    {errors.longitude && <div className="invalid-feedback">{errors.longitude.message}</div>}
                </div>
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
                    onClick={() => navigate("/administration/branch-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : branchToEdit ? 'Actualizar Sucursal' : 'Registrar Sucursal'}
                </button>
            </div>
        </form>
    );
}


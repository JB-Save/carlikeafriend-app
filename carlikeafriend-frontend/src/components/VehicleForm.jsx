import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useVehicleForm } from '../hooks/useVehicleForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { vehicleSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const VehicleForm = ({ vehicleToEdit, onVehicleSaved }) => {

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    const {
        allProducts,
        allBranches,
        allVehicleStatus,
        isLoadingProduct,
        isLoadingBranch,
        isLoadingVehicleStatus,
        productError,
        branchError,
        vehicleStatusError,
        error: apiError,
        isSubmittingForm,
        submitVehicleData
    } = useVehicleForm(vehicleToEdit, onVehicleSaved);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(vehicleSchema)
    });

    useEffect(() => {
        if (vehicleToEdit && !isLoadingProduct && !isLoadingBranch && !isLoadingVehicleStatus) {
            reset({
                licensePlate: vehicleToEdit.licensePlate,
                vin: vehicleToEdit.vin,
                currentMileage: vehicleToEdit.currentMileage,
                color: vehicleToEdit.color,
                year: vehicleToEdit.year,
                productId: vehicleToEdit.product.id.toString(),
                currentBranchId: vehicleToEdit.currentBranch.id.toString(),
                status: vehicleToEdit.status
            });
        }
    }, [vehicleToEdit, isLoadingProduct, isLoadingBranch, isLoadingVehicleStatus, reset]);

    const isLoading = isSubmittingForm || isLoadingProduct || isLoadingBranch || isLoadingVehicleStatus;

    return (
        <form onSubmit={handleSubmit(submitVehicleData)}>
            <div className="row">
                <div className="col-md-6 mb-3">
                    <label htmlFor="licensePlate" className="form-label fw-bold">Placa/Matrícula</label>
                    <input
                        type="text"
                        id="licensePlate"
                        className={`form-control ${errors.licensePlate ? 'is-invalid' : ''}`}
                        {...register('licensePlate')}
                        disabled={isLoading}
                    />
                    {errors.licensePlate && <div className="invalid-feedback">{errors.licensePlate.message}</div>}
                </div>
                <div className=" col-md-6 mb-3">
                    <label htmlFor="vin" className="form-label fw-bold">VIN</label>
                    <input
                        type="text"
                        id="vin"
                        className={`form-control ${errors.vin ? 'is-invalid' : ''}`}
                        {...register('vin')}
                        disabled={isLoading}
                    />
                    {errors.vin && <div className="invalid-feedback">{errors.vin.message}</div>}
                </div>
            </div>
            <div className="row">
                <div className="col-md-4 mb-3">
                    <label htmlFor="currentMileage" className="form-label fw-bold">kilometraje</label>
                    <input
                        type="number"
                        id="currentMileage"
                        step="1"
                        className={`form-control ${errors.currentMileage ? 'is-invalid' : ''}`}
                        {...register('currentMileage')}
                        disabled={isLoading}
                    />
                    {errors.currentMileage && <div className="invalid-feedback">{errors.currentMileage.message}</div>}
                </div>
                <div className="col-md-4 mb-3">
                    <label htmlFor="color" className="form-label fw-bold">Color</label>
                    <input
                        type="text"
                        id="color"
                        className={`form-control ${errors.color ? 'is-invalid' : ''}`}
                        {...register('color')}
                        disabled={isLoading}
                    />
                    {errors.color && <div className="invalid-feedback">{errors.color.message}</div>}
                </div>
                <div className="col-md-4 mb-3">
                    <label htmlFor="year" className="form-label fw-bold">Año modelo</label>
                    <input
                        type="number"
                        id="year"
                        step="1"
                        className={`form-control ${errors.year ? 'is-invalid' : ''}`}
                        {...register('year')}
                        disabled={isLoading}
                    />
                    {errors.year && <div className="invalid-feedback">{errors.year.message}</div>}
                </div>
            </div>
            <div className="mb-3">
                <label htmlFor="productId" className="form-label fw-bold">Referencia</label>
                {productError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{productError}</small>
                    </div>
                )}
                <select
                    id="productId"
                    className={`form-select ${errors.productId ? 'is-invalid' : ''}`}
                    {...register('productId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona una Referencia...
                    </option>
                    {allProducts.map((product) => (
                        <option key={product.id} value={product.id}>
                            {product.name}
                        </option>
                    ))}
                </select>
                {errors.productId && <div className="invalid-feedback">{errors.productId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="currentBranchId" className="form-label fw-bold">Sucursal Actual</label>
                {branchError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{branchError}</small>
                    </div>
                )}
                <select
                    id="currentBranchId"
                    className={`form-select ${errors.currentBranchId ? 'is-invalid' : ''}`}
                    {...register('currentBranchId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona una Sucursal...
                    </option>
                    {allBranches.map((branch) => (
                        <option key={branch.id} value={branch.id}>
                            {branch.name}
                        </option>
                    ))}
                </select>
                {errors.currentBranchId && <div className="invalid-feedback">{errors.currentBranchId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="status" className="form-label fw-bold">Estado</label>
                {vehicleStatusError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{vehicleStatusError}</small>
                    </div>
                )}
                <select
                    id="status"
                    className={`form-select ${errors.status ? 'is-invalid' : ''}`}
                    {...register('status')}
                    disabled={isLoading}
                >
                    <option value="">
                        Seleccione un Estado...
                    </option>
                    {allVehicleStatus.map((status) => (
                        <option key={status.value} value={status.value}>
                            {status.label}
                        </option>
                    ))}
                </select>
                {errors.status && <div className="invalid-feedback">{errors.status.message}</div>}
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
                    onClick={() => navigate("/administration/vehicle-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : vehicleToEdit ? 'Actualizar Vehículo' : 'Registrar Vehículo'}
                </button>
            </div>
        </form>
    );
}


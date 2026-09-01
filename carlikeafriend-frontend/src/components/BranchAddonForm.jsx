import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useBranchAddonForm } from '../hooks/useBranchAddonForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { inventorySchema } from '../utils/validationSchema';

export const BranchAddonForm = ({ addonToEdit, onInventoryAssigned }) => {
    const navigate = useNavigate();

    const {
        allBranches,
        allAddons,
        isLoadingBranch,
        isLoadingAddon,
        branchError,
        addonError,
        error: apiError,
        isSubmittingForm,
        submitInventoryData
    } = useBranchAddonForm(onInventoryAssigned);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        resolver: yupResolver(inventorySchema)
    });

    useEffect(() => {
        if (addonToEdit && !isLoadingBranch && !isLoadingAddon) {
            reset({
                branchId: addonToEdit.addonId.toString(),
                addonId: addonToEdit.addonId.toString(),
                totalStock: addonToEdit.totalStock
            });
        }
    }, [addonToEdit, isLoadingBranch, isLoadingAddon, reset]);

    const isLoading = isSubmittingForm || isLoadingBranch || isLoadingAddon;

    return (
        <form onSubmit={handleSubmit(submitInventoryData)}>
            <div className="mb-3">
                <label htmlFor="branchId" className="form-label fw-bold">Sucursal</label>
                {branchError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{branchError}</small>
                    </div>
                )}
                <select
                    id="branchId"
                    className={`form-select ${errors.branchId ? 'is-invalid' : ''}`}
                    {...register('branchId')}
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
                {errors.branchId && <div className="invalid-feedback">{errors.branchId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="addonId" className="form-label fw-bold">Extra (Addon)</label>
                {addonError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{addonError}</small>
                    </div>
                )}
                <select
                    id="addonId"
                    className={`form-select ${errors.addonId ? 'is-invalid' : ''}`}
                    {...register('addonId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona un Extra...
                    </option>
                    {allAddons.map((addon) => (
                        <option key={addon.addonId} value={addon.addonId}>
                            {addon.name} - ${addon.currentPrice}
                        </option>
                    ))}
                </select>
                {errors.addonId && <div className="invalid-feedback">{errors.addonId.message}</div>}
            </div>

            <div className="mb-3">
                <label htmlFor="totalStock" className="form-label fw-bold">Stock Total Físico</label>
                <input
                    type="number"
                    id="totalStock"
                    step="1"
                    className={`form-control ${errors.totalStock ? 'is-invalid' : ''}`}
                    {...register('totalStock')}
                    disabled={isLoading}
                />
                {errors.totalStock && <div className="invalid-feedback">{errors.totalStock.message}</div>}
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
                    onClick={() => navigate("/administration/branchAddon-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : addonToEdit ? 'Actualizar Inventario' : 'Asignar Inventario'}
                </button>
            </div>
        </form>
    );
}
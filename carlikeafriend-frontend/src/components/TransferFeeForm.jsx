import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useTransferFeeForm } from '../hooks/useTransferFeeForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { transferFeeSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const TransferFeeForm = ({ transferFeeToEdit, onTransferFeeSaved }) => {

    const navigate = useNavigate();
    const {
        allBranches,
        isLoadingBranch,
        branchError,
        error: apiError,
        isSubmittingForm,
        submitFeeData
    } = useTransferFeeForm(transferFeeToEdit, onTransferFeeSaved);

    const { register, handleSubmit, reset, watch, formState: { errors } } = useForm({
        resolver: yupResolver(transferFeeSchema),
        defaultValues: { originBranchId: '', destinationBranchId: '' },
        mode: 'onChange'
    });

    useEffect(() => {
        if (transferFeeToEdit && !isLoadingBranch) {
            reset({
                originBranchId: transferFeeToEdit.originBranch.id.toString(),
                destinationBranchId: transferFeeToEdit.destinationBranch.id.toString(),
                feeAmount: transferFeeToEdit.feeAmount
            });
        }
    }, [transferFeeToEdit, isLoadingBranch, reset]);

    const selectedOrigin = watch('originBranchId');
    const selectedDestination = watch('destinationBranchId');

    const filteredOrigin = allBranches.filter(branch => String(branch.id) !== selectedDestination);
    const filteredDestination = allBranches.filter(branch => String(branch.id) !== selectedOrigin);

    const isLoading = isSubmittingForm || isLoadingBranch;


    return (
        <form onSubmit={handleSubmit(submitFeeData)}>
            <div className="mb-3">
                <label htmlFor="originBranchId" className="form-label fw-bold">Sucursal de Origen</label>
                {branchError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{branchError}</small>
                    </div>
                )}
                <select
                    id="originBranchId"
                    className={`form-select ${errors.originBranchId ? 'is-invalid' : ''}`}
                    {...register('originBranchId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona Origen...
                    </option>
                    {filteredOrigin.map((branch) => (
                        <option key={branch.id} value={branch.id}>
                            {branch.name}
                        </option>
                    ))}
                </select>
                {errors.originBranchId && <div className="invalid-feedback">{errors.originBranchId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="destinationBranchId" className="form-label fw-bold">Sucursal de Destino</label>
                {branchError && (
                    <div className="alert alert-danger p-2 w-100">
                        <small><strong>¡Error! </strong>{branchError}</small>
                    </div>
                )}
                <select
                    id="destinationBranchId"
                    className={`form-select ${errors.destinationBranchId ? 'is-invalid' : ''}`}
                    {...register('destinationBranchId')}
                    disabled={isLoading}
                >
                    <option value="">
                        Selecciona Destino...
                    </option>
                    {filteredDestination.map((branch) => (
                        <option key={branch.id} value={branch.id}>
                            {branch.name}
                        </option>
                    ))}
                </select>
                {errors.destinationBranchId && <div className="invalid-feedback">{errors.destinationBranchId.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="feeAmount" className="form-label fw-bold">Tarifa</label>
                <input
                    type="number"
                    id="feeAmount"
                    step="0.01"
                    className={`form-control ${errors.feeAmount ? 'is-invalid' : ''}`}
                    {...register('feeAmount')}
                    disabled={isLoading}
                />
                {errors.feeAmount && <div className="invalid-feedback">{errors.feeAmount.message}</div>}
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
                    onClick={() => navigate("/administration/transferFee-list")}
                    disabled={isLoading}
                ><i className="bi bi-arrow-left me-2"></i>
                    Regresar
                </button>
                <button
                    type="submit"
                    className="btn btn-success rounded-3 px-4 shadow-sm"
                    disabled={isLoading}
                ><i className="bi bi-floppy me-1"></i>
                    {isLoading ? 'Guardando...' : transferFeeToEdit ? 'Actualizar Tarifa' : 'Registrar Tarifa'}
                </button>
            </div>
        </form>
    );
}


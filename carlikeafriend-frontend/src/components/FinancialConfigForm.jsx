import { useFinancialConfigForm } from '../hooks/useFinancialConfigForm';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { financialSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar Configuración Financiera
export const FinancialConfigForm = ({ onFinancialConfigSaved }) => {
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    financialConfig,
    isLoadingFinancialConfig,
    error: apiError,
    isSubmittingForm,
    submitFinancialData
  } = useFinancialConfigForm(onFinancialConfigSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(financialSchema)
  });

  useEffect(() => {
    if (financialConfig && !isLoadingFinancialConfig) {
      reset({
        taxRate: financialConfig.taxRate,
        defaultTransferFee: financialConfig.defaultTransferFee,
        basicInsuranceDepositMultiplier: financialConfig.basicInsuranceDepositMultiplier,
        premiumInsuranceDepositMultiplier: financialConfig.premiumInsuranceDepositMultiplier,
        fullCoverageDepositMultiplier: financialConfig.fullCoverageDepositMultiplier,
        insuranceBasicRate: financialConfig.insuranceBasicRate,
        insurancePremiumRate: financialConfig.insurancePremiumRate,
        insuranceFullCoverageRate: financialConfig.insuranceFullCoverageRate,
        penaltyWindowHours: financialConfig.penaltyWindowHours,
        cancellationPenaltyRate: financialConfig.cancellationPenaltyRate,
        noShowPenaltyRate: financialConfig.noShowPenaltyRate,
        maxRentalDays: financialConfig.maxRentalDays
      });
    }
  }, [financialConfig, isLoadingFinancialConfig, reset]);

  const isLoading = isSubmittingForm || isLoadingFinancialConfig;

  return (
    <form onSubmit={handleSubmit(submitFinancialData)} >
      <h5 className="form-title border-bottom pb-2 mb-3">Impuesto y Tarifa de Transferencia</h5>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="taxRate" className="form-label fw-bold">IVA</label>
          <input
            type="number"
            id="taxRate"
            step="0.01"
            className={`form-control ${errors.taxRate ? 'is-invalid' : ''}`}
            {...register('taxRate')}
            disabled={isLoading}
          />
          {errors.taxRate && <div className="invalid-feedback">{errors.taxRate.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="defaultTransferFee" className="form-label fw-bold">Tarifa de Transferencia Predeterminada</label>
          <input
            type="number"
            id="defaultTransferFee"
            step="0.01"
            className={`form-control ${errors.defaultTransferFee ? 'is-invalid' : ''}`}
            {...register('defaultTransferFee')}
            disabled={isLoading}
          />
          {errors.defaultTransferFee && <div className="invalid-feedback">{errors.defaultTransferFee.message}</div>}
        </div>
      </div>
      <h5 className="form-title border-bottom pb-2 mb-3">Tasas de Depósito de Seguro</h5>
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="basicInsuranceDepositMultiplier" className="form-label fw-bold">Básico</label>
          <input
            type="number"
            id="basicInsuranceDepositMultiplier"
            step="0.01"
            className={`form-control ${errors.basicInsuranceDepositMultiplier ? 'is-invalid' : ''}`}
            {...register('basicInsuranceDepositMultiplier')}
            disabled={isLoading}
          />
          {errors.basicInsuranceDepositMultiplier && <div className="invalid-feedback">{errors.basicInsuranceDepositMultiplier.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="premiumInsuranceDepositMultiplier" className="form-label fw-bold">Premium</label>
          <input
            type="number"
            id="premiumInsuranceDepositMultiplier"
            step="0.01"
            className={`form-control ${errors.premiumInsuranceDepositMultiplier ? 'is-invalid' : ''}`}
            {...register('premiumInsuranceDepositMultiplier')}
            disabled={isLoading}
          />
          {errors.premiumInsuranceDepositMultiplier && <div className="invalid-feedback">{errors.premiumInsuranceDepositMultiplier.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="fullCoverageDepositMultiplier" className="form-label fw-bold">Cobertura Total</label>
          <input
            type="number"
            id="fullCoverageDepositMultiplier"
            step="0.01"
            className={`form-control ${errors.fullCoverageDepositMultiplier ? 'is-invalid' : ''}`}
            {...register('fullCoverageDepositMultiplier')}
            disabled={isLoading}
          />
          {errors.fullCoverageDepositMultiplier && <div className="invalid-feedback">{errors.fullCoverageDepositMultiplier.message}</div>}
        </div>
      </div>
      <h5 className="form-title border-bottom pb-2 mb-3">Tarifas de Seguros</h5>
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="insuranceBasicRate" className="form-label fw-bold">Básico</label>
          <input
            type="number"
            id="insuranceBasicRate"
            step="0.01"
            className={`form-control ${errors.insuranceBasicRate ? 'is-invalid' : ''}`}
            {...register('insuranceBasicRate')}
            disabled={isLoading}
          />
          {errors.insuranceBasicRate && <div className="invalid-feedback">{errors.insuranceBasicRate.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="insurancePremiumRate" className="form-label fw-bold">Premium</label>
          <input
            type="number"
            id="insurancePremiumRate"
            step="0.01"
            className={`form-control ${errors.insurancePremiumRate ? 'is-invalid' : ''}`}
            {...register('insurancePremiumRate')}
            disabled={isLoading}
          />
          {errors.insurancePremiumRate && <div className="invalid-feedback">{errors.insurancePremiumRate.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="insuranceFullCoverageRate" className="form-label fw-bold">Cobertura Total</label>
          <input
            type="number"
            id="insuranceFullCoverageRate"
            step="0.01"
            className={`form-control ${errors.insuranceFullCoverageRate ? 'is-invalid' : ''}`}
            {...register('insuranceFullCoverageRate')}
            disabled={isLoading}
          />
          {errors.insuranceFullCoverageRate && <div className="invalid-feedback">{errors.insuranceFullCoverageRate.message}</div>}
        </div>
      </div>
      <h5 className="form-title border-bottom pb-2 mb-3">Ventana y Tasas de Penalización</h5>
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="penaltyWindowHours" className="form-label fw-bold">Ventana en Horas</label>
          <input
            type="number"
            id="penaltyWindowHours"
            step="1"
            className={`form-control ${errors.penaltyWindowHours ? 'is-invalid' : ''}`}
            {...register('penaltyWindowHours')}
            disabled={isLoading}
          />
          {errors.penaltyWindowHours && <div className="invalid-feedback">{errors.penaltyWindowHours.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="cancellationPenaltyRate" className="form-label fw-bold">Tasa por Cancelación</label>
          <input
            type="number"
            id="cancellationPenaltyRate"
            step="0.01"
            className={`form-control ${errors.cancellationPenaltyRate ? 'is-invalid' : ''}`}
            {...register('cancellationPenaltyRate')}
            disabled={isLoading}
          />
          {errors.cancellationPenaltyRate && <div className="invalid-feedback">{errors.cancellationPenaltyRate.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="noShowPenaltyRate" className="form-label fw-bold">Tasa por No Presentación</label>
          <input
            type="number"
            id="noShowPenaltyRate"
            step="0.01"
            className={`form-control ${errors.noShowPenaltyRate ? 'is-invalid' : ''}`}
            {...register('noShowPenaltyRate')}
            disabled={isLoading}
          />
          {errors.noShowPenaltyRate && <div className="invalid-feedback">{errors.noShowPenaltyRate.message}</div>}
        </div>
      </div>
      <h5 className="form-title border-bottom pb-2 mb-3">Alquiler</h5>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="maxRentalDays" className="form-label fw-bold">Días Máximos de Alquiler</label>
          <input
            type="number"
            id="maxRentalDays"
            step="1"
            className={`form-control ${errors.maxRentalDays ? 'is-invalid' : ''}`}
            {...register('maxRentalDays')}
            disabled={isLoading}
          />
          {errors.maxRentalDays && <div className="invalid-feedback">{errors.maxRentalDays.message}</div>}
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
          onClick={() => navigate("/administration/")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar al Panel
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : 'Actualizar Configuración Financiera'}
        </button>
      </div>
    </form>
  );
}


import { useState, useEffect, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { API_CONFIG } from '../config/apiConfig';

export const useFinancialConfigForm = (onFinancialConfigSaved) => {

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const navigate = useNavigate();

  const [financialConfig, setFinancialConfig] = useState(null);
  const [isLoadingFinancialConfig, setIsLoadingFinancialConfig] = useState(false);
  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);


  useEffect(() => {
    const fetchFinancialConfig = async () => {
      setIsLoadingFinancialConfig(true);
      setError(null);

      try {

        const response = await fetch(API_CONFIG.PRIVATE_FINANCIAL_CONFIG, {
          method: 'GET',
          headers: { 'Authorization': `Bearer ${token}` }
        });

        if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

        if (response.ok) {
          const data = await response.json();
          setFinancialConfig(data);
        } else {
          // Manejo de otros errores (400, 500, etc.)
          const msg = await extractErrorMessage(response);
          throw new Error(msg);
        }
      } catch (error) {
        console.error("Error cargando datos financieros: ", error);
        const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
        setError(message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingFinancialConfig(false);
      }
    };

    if (token) {
      fetchFinancialConfig();
    }

  }, [token, navigate, logout]);

  const submitFinancialData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);

    const payload = {
      ...data,
      taxRate: Number(data.taxRate),
      defaultTransferFee: Number(data.defaultTransferFee),
      basicInsuranceDepositMultiplier: Number(data.basicInsuranceDepositMultiplier),
      premiumInsuranceDepositMultiplier: Number(data.premiumInsuranceDepositMultiplier),
      fullCoverageDepositMultiplier: Number(data.fullCoverageDepositMultiplier),
      insuranceBasicRate: Number(data.insuranceBasicRate),
      insurancePremiumRate: Number(data.insurancePremiumRate),
      insuranceFullCoverageRate: Number(data.insuranceFullCoverageRate),
      penaltyWindowHours: Number(data.penaltyWindowHours),
      cancellationPenaltyRate: Number(data.cancellationPenaltyRate),
      noShowPenaltyRate: Number(data.noShowPenaltyRate),
      maxRentalDays: Number(data.maxRentalDays)
    }

    try {
      
      // Este formulario es solo para EDITAR (PUT).
      const method = 'PUT';
      if (!financialConfig) {
        throw new Error("Este formulario solo permite actualizar la configuración Financiera.");
      }

      const response = await fetch(API_CONFIG.PRIVATE_FINANCIAL_CONFIG, {
        method: method,
        headers: {
          'Content-type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onFinancialConfigSaved) onFinancialConfigSaved();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando configuración financiera:", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    financialConfig,
    isLoadingFinancialConfig,
    error,
    isSubmittingForm,
    submitFinancialData
  };
}
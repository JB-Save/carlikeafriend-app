import { useState, useEffect, useMemo } from 'react';
import { calculateRentalDays } from '../utils/dateHelpers';
import { API_CONFIG } from '../config/apiConfig';

// Caché en memoria global (Singleton Pattern)
let configCache = null;
let transferFeesCache = null;
let fetchPromise = null;


// 1. GESTOR DE PETICIONES Y CACHÉ (Exportable)
export const fetchFinancialData = () => {
    // Si ya tenemos los datos en memoria, los devolvemos inmediatamente
    if (configCache && transferFeesCache) {
        return Promise.resolve({ config: configCache, fees: transferFeesCache });
    }

    // Si no hay una petición en curso, la iniciamos
    if (!fetchPromise) {
        fetchPromise = Promise.all([
            fetch(API_CONFIG.PUBLIC_FINANCIAL_CONFIG).then(res => res.json()),
            fetch(API_CONFIG.TRANSFER_FEES).then(res => res.json())
        ]).then(([configData, feesData]) => {
            configCache = configData;
            transferFeesCache = feesData;
            return { config: configData, fees: feesData };
        }).catch(err => {
            console.error("Error cargando configuración financiera:", err);
            // Reseteamos la promesa en caso de error para permitir reintentos
            fetchPromise = null;
            throw err;
        });
    }

    return fetchPromise;
};

// 2. FUNCIÓN PURA MATEMÁTICA
export const calculateDynamicPrice = (product, bookingData, financialConfig, transferFees, selectedExtras = [], selectedInsurance = 'BASIC') => {
    if (!product || !financialConfig) return null;

    let pickup = new Date();
    let dropoff = new Date(pickup.getTime() + (24 * 60 * 60 * 1000));
    let hasDates = false;

    if (bookingData?.dateRange?.[0] && bookingData?.dateRange?.[1] && bookingData?.pickupTime && bookingData?.returnTime) {
        pickup = new Date(bookingData.dateRange[0]);
        pickup.setHours(bookingData.pickupTime.getHours(), bookingData.pickupTime.getMinutes(), 0, 0);

        dropoff = new Date(bookingData.dateRange[1]);
        dropoff.setHours(bookingData.returnTime.getHours(), bookingData.returnTime.getMinutes(), 0, 0);
        hasDates = true;
    }

    let rentalDays = 1;
    if (hasDates && dropoff > pickup) {
        rentalDays = calculateRentalDays(pickup, dropoff);
    }

    rentalDays = Math.min(rentalDays, financialConfig.maxRentalDays || 30);

    const baseCost = product.price * rentalDays;

    const insuranceRates = {
        BASIC: financialConfig.insuranceBasicRate || 0,
        PREMIUM: financialConfig.insurancePremiumRate || 0,
        FULL_COVERAGE: financialConfig.insuranceFullCoverageRate || 0
    };
    const insuranceCost = insuranceRates[selectedInsurance] * rentalDays;

    let transferFeeAmount = 0.0;
    if (bookingData?.differentReturnBranch && bookingData?.pickupBranch && bookingData?.returnBranch) {
        const originId = bookingData.pickupBranch.id;
        const destId = bookingData.returnBranch.id;

        const specificFee = transferFees?.find(
            tf => tf.originBranch?.id === originId && tf.destinationBranch?.id === destId
        );
        transferFeeAmount = specificFee ? specificFee.feeAmount : financialConfig.defaultTransferFee;
    }

    const extrasCost = selectedExtras.reduce((acc, extra) => {
        const quantity = extra.selectedQuantity || 1;
        if (extra.chargeType === 'PER_DAY') {
            const effectiveDays = extra.maxChargeableDays ? Math.min(rentalDays, extra.maxChargeableDays) : rentalDays;
            return acc + (extra.currentPrice * quantity * effectiveDays);
        }
        return acc + (extra.currentPrice * quantity);
    }, 0);

    const subtotal = baseCost + insuranceCost + transferFeeAmount + extrasCost;
    const taxAmount = subtotal * (financialConfig.taxRate || 0.19);
    const total = subtotal + taxAmount;
    const depositAmount = (product.baseDepositAmount || 0) * (financialConfig[`${selectedInsurance.toLowerCase()}InsuranceDepositMultiplier`] || 1);

    return { hasDates, rentalDays, baseCost, insuranceCost, transferFeeAmount, extrasCost, subtotal, taxAmount, total, depositAmount };
};

// 3. HELPER SIMPLIFICADO (Exclusivo para los topes del Slider de Filtros)
export const calculateSliderLimitsPrice = (dailyRate, pickupDate, returnDate, branchId, returnBranchId, config, transferFees) => {
    if (!pickupDate || !returnDate) return dailyRate;

    let rentalDays = 1;
    if (returnDate > pickupDate) {
        rentalDays = calculateRentalDays(pickupDate, returnDate);
    }
    rentalDays = Math.min(rentalDays, config.maxRentalDays || 30);

    const insuranceCost = (config?.insuranceBasicRate || 0) * rentalDays;

    let transferFee = 0.0;
    if (returnBranchId && branchId && returnBranchId !== branchId) {

        const specificFee = transferFees?.find(
            tf => tf.originBranch?.id === branchId && tf.destinationBranch?.id === returnBranchId
        );
        transferFee = specificFee ? specificFee.feeAmount : (config?.defaultTransferFee || 0);
    }

    const constantFees = insuranceCost + transferFee;
    const baseCost = dailyRate * rentalDays;
    const subtotal = baseCost + constantFees;
    const taxRate = config?.taxRate || 0;

    return subtotal * (1 + taxRate);
};


// 4. HOOK (Para usar en las tarjetas)
export const usePricing = (product, bookingData, selectedExtras = [], selectedInsurance = 'BASIC') => {
    const [financialConfig, setFinancialConfig] = useState(configCache);
    const [transferFees, setTransferFees] = useState(transferFeesCache);
    const [isLoading, setIsLoading] = useState(!configCache || !transferFeesCache);

    useEffect(() => {
        let isMounted = true;

        // Consumimos el gestor centralizado
        fetchFinancialData().then(({ config, fees }) => {
            if (isMounted) {
                setFinancialConfig(config);
                setTransferFees(fees);
                setIsLoading(false);
            }
        });

        return () => { isMounted = false; };
    }, []);

    const pricingDetails = useMemo(() => {
        return calculateDynamicPrice(product, bookingData, financialConfig, transferFees, selectedExtras, selectedInsurance);
    }, [product, bookingData, financialConfig, transferFees, selectedExtras, selectedInsurance]);

    return { pricingDetails, isLoadingPricing: isLoading };
};
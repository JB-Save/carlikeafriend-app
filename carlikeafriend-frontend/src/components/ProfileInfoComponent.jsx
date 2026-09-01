import React, { useState, useContext, useEffect, useMemo } from 'react';
import { ToastNotification } from './ToastNotification';
import { useUserAccount } from '../hooks/useUserAccount';
import { Controller, useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Country, State } from 'country-state-city';
import PhoneInput from 'react-phone-number-input';
import 'react-phone-number-input/style.css';
import { userProfileSchema } from '../utils/validationSchema';

export const ProfileInfoComponent = () => {

    const {
        userAccountData,
        documentTypes,
        showToast,
        toastMessage,
        toastType,
        isLoading,
        setShowToast,
        submitUserData } = useUserAccount();

    const { register, handleSubmit, control, watch, reset, setValue, formState: { errors, dirtyFields } } = useForm({
        resolver: yupResolver(userProfileSchema),
    });

    // Setear datos iniciales cuando userAccountData esté disponible
    useEffect(() => {
        if (userAccountData && !isLoading) {
            reset({
                ...userAccountData,
            });
        }
    }, [userAccountData, isLoading, reset]);

    // Lógica de cascada para País -> Estado/Departamento
    const countries = useMemo(() => Country.getAllCountries(), []);
    const selectedCountryCode = watch('countryCode');
    const states = useMemo(() => {
        return selectedCountryCode ? State.getStatesOfCountry(selectedCountryCode) : [];
    }, [selectedCountryCode]);

    // Limpiar estado y ciudad si el país cambia
    useEffect(() => {
        // Si el campo 'countryCode' está "sucio" (fue modificado por el usuario)
        // limpiamos el estado y la ciudad.
        if (dirtyFields.countryCode) {
            setValue('stateCode', '');
            setValue('city', '');
        }
    }, [selectedCountryCode, setValue, dirtyFields.countryCode]);

    return (
        <div className="w-100">
            <div className="border-bottom pb-2 mb-4">
                <h3 className="h4 fw-bold my-account-block-title-color mb-0">
                    <i className="bi bi-person-lines-fill me-2 text-primary"></i> Información Personal
                </h3>
                <p className="my-account-block-text-muted small mb-0">Mantén tus datos al día para agilizar tus próximas reservas.</p>
            </div>

            <form onSubmit={handleSubmit(submitUserData)} className="row g-3">
                <div className="col-md-6">
                    <label htmlFor="name" className="form-label fw-semibold small my-account-form-text">Nombre *</label>
                    <input
                        type="text"
                        id="name"
                        className={`form-control rounded-3 ${errors.name ? 'is-invalid' : ''}`}
                        {...register('name')}
                        disabled={isLoading}
                    />
                    {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="lastName" className="form-label fw-semibold small my-account-form-text">Apellido *</label>
                    <input
                        type="text"
                        id="lastName"
                        className={`form-control rounded-3 ${errors.lastName ? 'is-invalid' : ''}`}
                        {...register('lastName')}
                        disabled={isLoading}
                    />
                    {errors.lastName && <div className="invalid-feedback">{errors.lastName.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="email" className="form-label fw-semibold small my-account-form-text">Correo Electrónico</label>
                    <input
                        type="email"
                        id="email"
                        className={`form-control rounded-3 bg-light ${errors.email ? 'is-invalid' : ''}`}
                        {...register('email')}
                        autoComplete="off"
                        disabled
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="phoneNumber" className="form-label fw-semibold small my-account-form-text">Teléfono de Contacto *</label>
                    <Controller
                        name="phoneNumber"
                        control={control}
                        render={({ field }) => (
                            <PhoneInput
                                {...field}
                                international
                                defaultCountry="CO"
                                id="phoneNumber"
                                className={`form-control rounded-3 p-0 d-flex ${errors.phoneNumber ? 'is-invalid border-danger' : ''}`}
                                style={{ '--PhoneInput-color--focus': 'transparent' }}
                                numberInputProps={{ className: 'form-control border-0', disabled: isLoading }}
                            />
                        )}
                    />
                    {errors.phoneNumber && <small className="text-danger mt-1 d-block">{errors.phoneNumber.message}</small>}
                </div>

                <div className="col-md-6">
                    <label htmlFor="documentType" className="form-label fw-semibold small my-account-form-text">Tipo de Documento *</label>
                    <select
                        id="documentType"
                        className={`form-select rounded-3 ${errors.documentType ? 'is-invalid' : ''}`}
                        {...register('documentType')}
                        disabled={isLoading}
                    >
                        <option value="">Seleccione...</option>
                        {documentTypes.map((docType) => (
                            <option key={docType.value} value={docType.value}>
                                {docType.label}
                            </option>
                        ))}
                    </select>
                    {errors.documentType && <div className="invalid-feedback">{errors.documentType.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="documentNumber" className="form-label fw-semibold small my-account-form-text">Número de Documento *</label>
                    <input
                        type="text"
                        id="documentNumber"
                        className={`form-control rounded-3 ${errors.documentNumber ? 'is-invalid' : ''}`}
                        {...register('documentNumber')}
                        disabled={isLoading}
                    />
                    {errors.documentNumber && <div className="invalid-feedback">{errors.documentNumber.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="nationality" className="form-label fw-semibold small my-account-form-text">Nacionalidad *</label>
                    <input
                        type="text"
                        id="nationality"
                        className={`form-control rounded-3 ${errors.nationality ? 'is-invalid' : ''}`}
                        {...register('nationality')}
                        disabled={isLoading}
                    />
                    {errors.nationality && <div className="invalid-feedback">{errors.nationality.message}</div>}
                </div>

                <div className="border-bottom py-2 my-4">
                    <h5 className="h6 fw-bold my-account-form-text mb-0"><i className="bi bi-geo-alt-fill me-2"></i>Ubicación y Residencia</h5>
                </div>

                <div className="col-md-4">
                    <label htmlFor="countryCode" className="form-label fw-semibold small my-account-form-text">País *</label>
                    <select
                        id="countryCode"
                        className={`form-select rounded-3 ${errors.countryCode ? 'is-invalid' : ''}`}
                        {...register('countryCode')}
                        disabled={isLoading}
                    >
                        <option value="">Seleccione un país...</option>
                        {countries.map(country => (
                            <option key={country.isoCode} value={country.isoCode}>{country.name}</option>
                        ))}
                    </select>
                    {errors.countryCode && <div className="invalid-feedback">{errors.countryCode.message}</div>}
                </div>
                <div className="col-md-4">
                    <label htmlFor="stateCode" className="form-label fw-semibold small my-account-form-text">Estado / Departamento *</label>
                    <select
                        id="stateCode"
                        className={`form-select rounded-3 ${errors.stateCode ? 'is-invalid' : ''}`}
                        {...register('stateCode')}
                        disabled={isLoading || !selectedCountryCode}
                    >
                        <option value="">Seleccione un estado...</option>
                        {states.map(state => (
                            <option key={state.isoCode} value={state.isoCode}>{state.name}</option>
                        ))}
                    </select>
                    {errors.stateCode && <div className="invalid-feedback">{errors.stateCode.message}</div>}
                </div>
                <div className="col-md-4">
                    <label htmlFor="city" className="form-label fw-semibold small my-account-form-text">Ciudad *</label>
                    <input
                        type="text"
                        id="city"
                        className={`form-control rounded-3 ${errors.city ? 'is-invalid' : ''}`}
                        {...register('city')}
                        disabled={isLoading}
                    />
                    {errors.city && <div className="invalid-feedback">{errors.city.message}</div>}
                </div>
                <div className="col-md-8">
                    <label htmlFor="address" className="form-label fw-semibold small my-account-form-text">Dirección de Residencia *</label>
                    <input
                        type="text"
                        id="address"
                        className={`form-control rounded-3 ${errors.address ? 'is-invalid' : ''}`}
                        {...register('address')}
                        disabled={isLoading}
                    />
                    {errors.address && <div className="invalid-feedback">{errors.address.message}</div>}
                </div>
                <div className="col-md-4">
                    <label htmlFor="zipCode" className="form-label fw-semibold small my-account-form-text">Código Postal *</label>
                    <input
                        type="text"
                        id="zipCode"
                        className={`form-control rounded-3 ${errors.zipCode ? 'is-invalid' : ''}`}
                        {...register('zipCode')}
                        disabled={isLoading}
                    />
                    {errors.zipCode && <div className="invalid-feedback">{errors.zipCode.message}</div>}
                </div>

                <div className="border-bottom py-2 my-4">
                    <h5 className="h6 fw-bold my-account-form-text mb-0"><i className="bi bi-card-checklist me-2"></i>Licencia de Conducción</h5>
                </div>

                <div className="col-md-4">
                    <label htmlFor="birthDate" className="form-label fw-semibold small my-account-form-text">Fecha de Nacimiento *</label>
                    <input
                        type="date"
                        id="birthDate"
                        className={`form-control rounded-3 ${errors.birthDate ? 'is-invalid' : ''}`}
                        {...register('birthDate')}
                        disabled={isLoading}
                    />
                    {errors.birthDate && <div className="invalid-feedback">{errors.birthDate.message}</div>}
                </div>
                <div className="col-md-4">
                    <label htmlFor="driverLicenseNumber" className="form-label fw-semibold small my-account-form-text">Nro. Licencia de Conducción *</label>
                    <input
                        type="text"
                        id="driverLicenseNumber"
                        className={`form-control rounded-3 ${errors.driverLicenseNumber ? 'is-invalid' : ''}`}
                        {...register('driverLicenseNumber')}
                        disabled={isLoading}
                    />
                    {errors.driverLicenseNumber && <div className="invalid-feedback">{errors.driverLicenseNumber.message}</div>}
                </div>
                <div className="col-md-4">
                    <label htmlFor="driverLicenseExpiry" className="form-label fw-semibold small my-account-form-text">Vencimiento Licencia *</label>
                    <input
                        type="date"
                        id="driverLicenseExpiry"
                        className={`form-control rounded-3 ${errors.driverLicenseExpiry ? 'is-invalid' : ''}`}
                        {...register('driverLicenseExpiry')}
                        disabled={isLoading}
                    />
                    {errors.driverLicenseExpiry && <div className="invalid-feedback">{errors.driverLicenseExpiry.message}</div>}
                </div>

                <div className="border-bottom py-2 my-4">
                    <h5 className="h6 fw-bold my-account-form-text mb-0"><i className="bi bi-telephone-plus-fill me-2"></i>Contacto de Emergencia</h5>
                </div>

                <div className="col-md-6">
                    <label htmlFor="emergencyContactName" className="form-label fw-semibold small my-account-form-text">Nombre de Contacto *</label>
                    <input
                        type="text"
                        id="emergencyContactName"
                        className={`form-control rounded-3 ${errors.emergencyContactName ? 'is-invalid' : ''}`}
                        {...register('emergencyContactName')}
                        disabled={isLoading}
                    />
                    {errors.emergencyContactName && <div className="invalid-feedback">{errors.emergencyContactName.message}</div>}
                </div>
                <div className="col-md-6">
                    <label htmlFor="emergencyContactPhone" className="form-label fw-semibold small my-account-form-text">Teléfono de Emergencia *</label>
                    <Controller
                        name="emergencyContactPhone"
                        control={control}
                        render={({ field }) => (
                            <PhoneInput
                                {...field}
                                international
                                defaultCountry="CO"
                                id="emergencyContactPhone"
                                className={`form-control rounded-3 p-0 d-flex ${errors.emergencyContactPhone ? 'is-invalid border-danger' : ''}`}
                                style={{ '--PhoneInput-color--focus': 'transparent' }}
                                numberInputProps={{ className: 'form-control border-0', disabled: isLoading }}
                            />
                        )}
                    />
                    {errors.emergencyContactPhone && <small className="text-danger mt-1 d-block">{errors.emergencyContactPhone.message}</small>}
                </div>

                <div className="col-12 d-flex justify-content-end mt-4">
                    <button
                        type="submit"
                        className="btn form-btn px-5 py-2 rounded-3 shadow-sm"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                Guardando...
                            </>
                        ) : 'Guardar Cambios'}
                    </button>
                </div>
            </form>

            <ToastNotification show={showToast} message={toastMessage} type={toastType} onClose={() => setShowToast(false)} />
        </div>
    );
};
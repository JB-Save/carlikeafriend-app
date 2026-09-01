import { useEffect, useMemo } from 'react';
import { useUserForm } from '../hooks/useUserForm';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Country, State } from 'country-state-city';
import PhoneInput from 'react-phone-number-input';
import 'react-phone-number-input/style.css';
import { userSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar usuarios
export const UserForm = ({ userToEdit, onUserSaved }) => {
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    allRoles,
    documentTypes,
    isLoadingRole,
    isLoadingDocType,
    roleError,
    docTypeError,
    error: apiError,
    isSubmittingForm,
    submitUserData,
  } = useUserForm(userToEdit, onUserSaved);

  const { register, handleSubmit, control, watch, reset, setValue, formState: { errors, dirtyFields } } = useForm({
    resolver: yupResolver(userSchema),
    defaultValues: { roleIds: [] } // Inicializar array vacío
  });

  // Setear datos iniciales cuando userToEdit esté disponible
  useEffect(() => {
    if (userToEdit && !isLoadingRole && !isLoadingDocType) {
      reset({
        ...userToEdit,
        roleIds: userToEdit.roleIds ? userToEdit.roleIds.map(roleId => roleId.toString()) : []
      });
    }
  }, [userToEdit, isLoadingRole, isLoadingDocType, reset]);

  // Lógica de cascada para País -> Estado/Departamento
  const countries = useMemo(() => Country.getAllCountries(), []);
  const selectedCountryCode = watch('countryCode');
  const states = useMemo(() => {
    return selectedCountryCode ? State.getStatesOfCountry(selectedCountryCode) : [];
  }, [selectedCountryCode]);

  // Limpiar estado y ciudad si el país cambia
  useEffect(() => {
    if (dirtyFields.countryCode) {
      setValue('stateCode', '');
      setValue('city', '');
    }
  }, [selectedCountryCode, setValue, dirtyFields.countryCode]);

  // Para deshabilitar toda la UI mientras carga/guarda
  const isLoading = isSubmittingForm || isLoadingRole || isLoadingDocType;


  return (
    <form onSubmit={handleSubmit(submitUserData)}>
      {/* SECCIÓN: Datos Personales y Contacto */}
      <h5 className="form-title border-bottom pb-2 mb-3"><i className="bi bi-person-vcard me-2"></i>Datos Personales y Contacto</h5>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="name" className="form-label small fw-bold">Nombre *</label>
          <input
            type="text"
            id="name"
            className={`form-control ${errors.name ? 'is-invalid' : ''}`}
            {...register('name')}
            disabled={isLoading}
          />
          {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="lastName" className="form-label fw-bold">Apellido *</label>
          <input
            type="text"
            id="lastName"
            className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
            {...register('lastName')}
            disabled={isLoading}
          />
          {errors.lastName && <div className="invalid-feedback">{errors.lastName.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="birthDate" className="form-label fw-bold">Fecha de Nacimiento *</label>
          <input
            type="date"
            id="birthDate"
            className={`form-control ${errors.birthDate ? 'is-invalid' : ''}`}
            {...register('birthDate')}
            disabled={isLoading}
          />
          {errors.birthDate && <div className="invalid-feedback">{errors.birthDate.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="phoneNumber" className="form-label fw-bold">Teléfono de Contacto *</label>
          <Controller
            name="phoneNumber"
            control={control}
            render={({ field }) => (
              <PhoneInput
                {...field}
                international
                defaultCountry="CO"
                id="phoneNumber"
                className={`form-control p-0 d-flex ${errors.phoneNumber ? 'is-invalid border-danger' : ''}`}
                style={{ '--PhoneInput-color--focus': 'transparent', border: 'none' }}
                numberInputProps={{ className: 'form-control border-0', disabled: isLoading }}
              />
            )}
          />
          {errors.phoneNumber && <small className="text-danger mt-1 d-block">{errors.phoneNumber.message}</small>}
        </div>
        <div className="col-12 mb-3">
          <label htmlFor="email" className="form-label fw-bold">Email</label>
          <div className="input-group">
            <span className="input-group-text">@</span>
            <input
              type="email"
              id="email"
              className={`form-control ${errors.email ? 'is-invalid' : ''}`}
              {...register('email')}
              autoComplete="off"
              disabled
            />
            {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
          </div>
        </div>
      </div>

      {/* SECCIÓN: Documentación y Licencia */}
      <h5 className="form-title border-bottom pb-2 mb-3 mt-4"><i className="bi bi-pass me-2"></i>Documentación y Conducción</h5>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="documentType" className="form-label fw-bold">Tipo de Documento *</label>
          {docTypeError && (
            <div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{docTypeError}</small>
            </div>
          )}
          <select
            id="documentType"
            className={`form-select ${errors.documentType ? 'is-invalid' : ''} `}
            {...register('documentType')}
            disabled={isLoading}
          >
            <option value="">
              Seleccione...
            </option>
            {documentTypes.map((docType) => (
              <option key={docType.value} value={docType.value}>
                {docType.label}
              </option>
            ))}
          </select>
          {errors.documentType && <div className="invalid-feedback">{errors.documentType.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="documentNumber" className="form-label fw-bold">Número de Documento *</label>
          <input
            type="text"
            id="documentNumber"
            className={`form-control ${errors.documentNumber ? 'is-invalid' : ''}`}
            {...register('documentNumber')}
            disabled={isLoading}
          />
          {errors.documentNumber && <div className="invalid-feedback">{errors.documentNumber.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="driverLicenseNumber" className="form-label fw-bold">Nro. Licencia de Conducción *</label>
          <input
            type="text"
            id="driverLicenseNumber"
            className={`form-control ${errors.driverLicenseNumber ? 'is-invalid' : ''}`}
            {...register('driverLicenseNumber')}
            disabled={isLoading}
          />
          {errors.driverLicenseNumber && <div className="invalid-feedback">{errors.driverLicenseNumber.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="driverLicenseExpiry" className="form-label fw-bold">Vencimiento Licencia *</label>
          <input
            type="date"
            id="driverLicenseExpiry"
            className={`form-control ${errors.driverLicenseExpiry ? 'is-invalid' : ''}`}
            {...register('driverLicenseExpiry')}
            disabled={isLoading}
          />
          {errors.driverLicenseExpiry && <div className="invalid-feedback">{errors.driverLicenseExpiry.message}</div>}
        </div>
      </div>

      {/* SECCIÓN: Datos de Residencia */}
      <h5 className="form-title border-bottom pb-2 mb-3 mt-4"><i className="bi bi-geo-alt me-2"></i>Ubicación y Residencia</h5>
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="nationality" className="form-label fw-bold">Nacionalidad *</label>
          <input
            type="text"
            id="nationality"
            className={`form-control ${errors.nationality ? 'is-invalid' : ''}`}
            {...register('nationality')}
            disabled={isLoading}
          />
          {errors.nationality && <div className="invalid-feedback">{errors.nationality.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="countryCode" className="form-label fw-bold">País *</label>
          <select
            id="countryCode"
            className={`form-select ${errors.countryCode ? 'is-invalid' : ''}`}
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
        <div className="col-md-4 mb-3">
          <label htmlFor="stateCode" className="form-label fw-bold">Estado / Departamento*</label>
          <select id="stateCode" className={`form-select ${errors.stateCode ? 'is-invalid' : ''}`}
            {...register('stateCode')}
            disabled={isLoading || !selectedCountryCode}>
            <option value="">Seleccione un estado...</option>
            {states.map(state => (
              <option key={state.isoCode} value={state.isoCode}>{state.name}</option>
            ))}
          </select>
          {errors.stateCode && <div className="invalid-feedback">{errors.stateCode.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="city" className="form-label fw-bold">Ciudad *</label>
          <input
            type="text"
            id="city"
            className={`form-control ${errors.city ? 'is-invalid' : ''}`}
            {...register('city')}
            disabled={isLoading}
          />
          {errors.city && <div className="invalid-feedback">{errors.city.message}</div>}
        </div>
        <div className="col-md-5 mb-3">
          <label htmlFor="address" className="form-label fw-bold">Dirección de Residencia *</label>
          <input
            type="text"
            id="address"
            className={`form-control ${errors.address ? 'is-invalid' : ''}`}
            {...register('address')}
            disabled={isLoading}
          />
          {errors.address && <div className="invalid-feedback">{errors.address.message}</div>}
        </div>
        <div className="col-md-3 mb-3">
          <label htmlFor="zipCode" className="form-label fw-bold">Código Postal *</label>
          <input
            type="text"
            id="zipCode"
            className={`form-control ${errors.zipCode ? 'is-invalid' : ''}`}
            {...register('zipCode')}
            disabled={isLoading}
          />
          {errors.zipCode && <div className="invalid-feedback">{errors.zipCode.message}</div>}
        </div>
      </div>

      {/* SECCIÓN: Contacto de Emergencia */}
      <h5 className="form-title border-bottom pb-2 mb-3 mt-4"><i className="bi bi-heart-pulse me-2"></i>Contacto de Emergencia</h5>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label htmlFor="emergencyContactName" className="form-label fw-bold">Nombre de Contacto *</label>
          <input
            type="text"
            id="emergencyContactName"
            className={`form-control ${errors.emergencyContactName ? 'is-invalid' : ''}`}
            {...register('emergencyContactName')}
            disabled={isLoading}
          />
          {errors.emergencyContactName && <div className="invalid-feedback">{errors.emergencyContactName.message}</div>}
        </div>
        <div className="col-md-6 mb-3">
          <label htmlFor="emergencyContactPhone" className="form-label fw-bold">Teléfono de Emergencia *</label>
          <Controller
            name="emergencyContactPhone"
            control={control}
            render={({ field }) => (
              <PhoneInput
                {...field}
                international
                defaultCountry="CO"
                id="emergencyContactPhone"
                className={`form-control p-0 d-flex ${errors.emergencyContactPhone ? 'is-invalid border-danger' : ''}`}
                style={{ '--PhoneInput-color--focus': 'transparent', border: 'none' }}
                numberInputProps={{ className: 'form-control border-0', disabled: isLoading }}
              />
            )}
          />
          {errors.emergencyContactPhone && <small className="text-danger mt-1 d-block">{errors.emergencyContactPhone.message}</small>}
        </div>
      </div>

      {/* SECCIÓN: Roles */}
      <fieldset className="mb-4 mt-4 border-0 p-0">
        <h5 className="form-title border-bottom pb-2 mb-3"><i className="bi bi-shield-lock me-2"></i>Roles de Acceso</h5>
        <div className={`border p-3 rounded-2 bg-light shadow-sm ${errors.roleIds ? 'border-danger' : ''}`}>
          {roleError &&
            (<div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{roleError}</small>
            </div>)}
          {isLoadingRole ? (
            <div className="text-center my-3"><div className="spinner-border spinner-border-sm" role="status"></div><p className="admin-panel-text-muted mt-2">Cargando los Roles...</p></div>
          ) : (
            <div className="row g-2">
              {allRoles.map((role) => (
                <div className="col-12 col-sm-6 col-xl-4" key={role.id}>
                  <div className="form-check">
                    <input
                      type="checkbox"
                      id={`role-${role.id}`}
                      value={role.id.toString()}
                      className="form-check-input"
                      {...register('roleIds')}
                      disabled={isLoading}
                    />
                    <label className="form-check-label user-select-none" htmlFor={`role-${role.id}`}>
                      {role.name}
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
          {errors.roleIds && <small className="text-danger mt-2 d-block">{errors.roleIds.message}</small>}
        </div>
      </fieldset>

      {
        apiError && (
          <div className="alert alert-danger shadow-sm" role="alert">
            <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
          </div>
        )
      }
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/user-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isSubmittingForm ? 'Guardando...' : userToEdit ? 'Actualizar Usuario' : ''}
        </button>
      </div>
    </form >
  );
}


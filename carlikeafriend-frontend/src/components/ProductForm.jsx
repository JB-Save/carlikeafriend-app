import { ImagePreviewList } from './ImagePreviewList';
import { ImageUploadInput } from './ImageUploadInput';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useProductForm } from '../hooks/useProductForm';
import { yupResolver } from '@hookform/resolvers/yup';
import { productSchema } from '../utils/validationSchema';
import { API_CONFIG } from '../config/apiConfig';

// Componente del formulario para crear/editar productos
export const ProductForm = ({ productToEdit, onProductSaved }) => {
  const MAX_IMAGES = API_CONFIG.MAX_IMAGES_FOR_PRODUCTS;
  const URL = API_CONFIG.PRODUCT_IMAGES_BASE;
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    allCategories,
    allFeatures,
    allMakes,
    allPolicies,
    isLoadingCategory,
    isLoadingFeature,
    isLoadingMake,
    isLoadingPolicy,
    categoryError,
    featureError,
    makeError,
    policyError,
    error: apiError,
    isSubmittingForm,
    newImages,
    existingImages,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    submitProductData
  } = useProductForm(productToEdit, MAX_IMAGES, onProductSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(productSchema),
    defaultValues: { categories: [], features: [], policies: [] }
  });

  useEffect(() => {
    if (productToEdit && !isLoadingMake && !isLoadingCategory && !isLoadingFeature && !isLoadingPolicy) {
      reset({
        name: productToEdit.name,
        makeId: productToEdit.make.id.toString(),
        description: productToEdit.description,
        passengerCapacity: productToEdit.passengerCapacity,
        baggageCapacity: productToEdit.baggageCapacity,
        numberOfDoors: productToEdit.numberOfDoors,
        categories: productToEdit.categories.map(category => category.id.toString()),
        features: productToEdit.features.map(feature => feature.id.toString()),
        policies: productToEdit.policies.map(policy => policy.id.toString())
      });
    }
  }, [productToEdit, isLoadingMake, isLoadingCategory, isLoadingFeature, isLoadingPolicy, reset]);

  // Para deshabilitar toda la UI mientras carga/guarda
  const isLoading = isSubmittingForm || isLoadingMake || isLoadingCategory || isLoadingFeature || isLoadingPolicy;

  return (
    <form onSubmit={handleSubmit(submitProductData)} >
      <div className="mb-3">
        <label htmlFor="name" className="form-label fw-bold">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          className={`form-control ${errors.name ? 'is-invalid' : ''}`}
          {...register('name')}
          disabled={isLoading}
        />
        {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
      </div>
      <div className="mb-3">
        <label htmlFor="makeId" className="form-label fw-bold">Marca</label>
        {makeError && (
          <div className="alert alert-danger p-2 w-100">
            <small><strong>¡Error! </strong>{makeError}</small>
          </div>
        )}
        <select
          id="makeId"
          className={`form-select ${errors.makeId ? 'is-invalid' : ''}`}
          {...register('makeId')}
          disabled={isLoading}
        >
          <option value="">
            Selecciona una Marca...
          </option>
          {allMakes.map((make) => (
            <option key={make.id} value={make.id}>
              {make.name}
            </option>
          ))}
        </select>
        {errors.makeId && <div className="invalid-feedback">{errors.makeId.message}</div>}
      </div>
      <div className="mb-3">
        <label htmlFor="description" className="form-label fw-bold">Descripción</label>
        <textarea
          id="description"
          rows="3"
          className={`form-control ${errors.description ? 'is-invalid' : ''}`}
          {...register('description')}
          disabled={isLoading}
        >
        </textarea>
        {errors.description && <div className="invalid-feedback">{errors.description.message}</div>}
      </div>
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="passengerCapacity" className="form-label fw-bold">Capacidad de Pasajeros</label>
          <input
            type="number"
            id="passengerCapacity"
            step="1"
            className={`form-control ${errors.passengerCapacity ? 'is-invalid' : ''}`}
            {...register('passengerCapacity')}
            disabled={isLoading}
          />
          {errors.passengerCapacity && <div className="invalid-feedback">{errors.passengerCapacity.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="baggageCapacity" className="form-label fw-bold">Capacidad de equipaje</label>
          <input
            type="number"
            id="baggageCapacity"
            step="1"
            className={`form-control ${errors.baggageCapacity ? 'is-invalid' : ''}`}
            {...register('baggageCapacity')}
            disabled={isLoading}
          />
          {errors.baggageCapacity && <div className="invalid-feedback">{errors.baggageCapacity.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="numberOfDoors" className="form-label fw-bold">Número de puertas</label>
          <input
            type="number"
            id="numberOfDoors"
            step="1"
            className={`form-control ${errors.numberOfDoors ? 'is-invalid' : ''}`}
            {...register('numberOfDoors')}
            disabled={isLoading}
          />
          {errors.numberOfDoors && <div className="invalid-feedback">{errors.numberOfDoors.message}</div>}
        </div>
      </div>
      <fieldset className="mb-4 mt-4 border-0 p-0">
        <legend className="form-label fw-bold float-none w-auto mb-2 m-0 fs-6 lh-base">Categorías</legend>
        <div className={`border p-3 rounded-2 bg-light shadow-sm ${errors.categories ? 'border-danger' : ''}`}>
          {categoryError &&
            (<div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{categoryError}</small>
            </div>)}
          {isLoadingCategory ? (
            <div className="text-center my-3"><div className="spinner-border spinner-border-sm" role="status"></div><p className="admin-panel-text-muted mt-2">Cargando categorías...</p></div>
          ) : (
            <div className="row g-2">
              {allCategories.map((category) => (
                <div className="col-12 col-sm-6 col-xl-4" key={category.id}>
                  <div className="form-check">
                    <input
                      type="checkbox"
                      id={`category-${category.id}`}
                      value={category.id.toString()}
                      className="form-check-input"
                      {...register('categories')}
                      disabled={isLoading}
                    />
                    <label className="form-check-label user-select-none" htmlFor={`category-${category.id}`}>
                      {category.name}
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
          {errors.categories && <small className="text-danger mt-2 d-block">{errors.categories.message}</small>}
        </div>
      </fieldset>
      <fieldset className="mb-4 mt-4 border-0 p-0">
        <legend className="form-label fw-bold float-none w-auto mb-2 m-0 fs-6 lh-base">Características</legend>
        <div className={`border p-3 rounded-2 bg-light shadow-sm ${errors.features ? 'border-danger' : ''}`}>
          {featureError &&
            (<div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{featureError}</small>
            </div>)}
          {isLoadingFeature ? (
            <div className="text-center my-3"><div className="spinner-border spinner-border-sm" role="status"></div><p className="admin-panel-text-muted mt-2">Cargando características...</p></div>
          ) : (
            <div className="row g-2">
              {allFeatures.map((feature) => (
                <div className="col-12 col-sm-6 col-xl-4" key={feature.id}>
                  <div className="form-check">
                    <input
                      type="checkbox"
                      id={`feature-${feature.id}`}
                      value={feature.id.toString()}
                      className="form-check-input"
                      {...register('features')}
                      disabled={isLoading}
                    />
                    <label className="form-check-label user-select-none" htmlFor={`feature-${feature.id}`}>
                      {feature.name}
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
          {errors.features && <small className="text-danger mt-2 d-block">{errors.features.message}</small>}
        </div>
      </fieldset>
      <fieldset className="mb-4 mt-4 border-0 p-0">
        <legend className="form-label fw-bold float-none w-auto mb-2 m-0 fs-6 lh-base">Políticas</legend>
        <div className={`border p-3 rounded-2 bg-light shadow-sm ${errors.policies ? 'border-danger' : ''}`}>
          {policyError &&
            (<div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{policyError}</small>
            </div>)}
          {isLoadingPolicy ? (
            <div className="text-center my-3"><div className="spinner-border spinner-border-sm" role="status"></div><p className="admin-panel-text-muted mt-2">Cargando políticas...</p></div>
          ) : (
            <div className="row g-2">
              {allPolicies.map((policy) => (
                <div className="col-12 col-sm-6 col-xl-4" key={policy.id}>
                  <div className="form-check">
                    <input
                      type="checkbox"
                      id={`policy-${policy.id}`}
                      value={policy.id.toString()}
                      className="form-check-input"
                      {...register('policies')}
                      disabled={isLoading}
                    />
                    <label className="form-check-label user-select-none" htmlFor={`policy-${policy.id}`}>
                      {policy.name}
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
          {errors.policies && <small className="text-danger mt-2 d-block">{errors.policies.message}</small>}
        </div>
      </fieldset>

      <ImagePreviewList
        URL={URL}
        images={existingImages}
        onRemoveImage={handleRemoveExistingImage}
      />

      <ImageUploadInput
        maxImages={MAX_IMAGES}
        availableSlots={availableSlots}
        newImages={newImages}
        onFileChange={handleNewImageChange}
        fileInputRef={fileInputRef}
        uploadError={imageUploadError}
        canAddMoreImages={canAddMoreImages}
        onDeleteUploadedFile={handleRemoveNewImageFile}
      />

      {apiError && (
        <div className="alert alert-danger shadow-sm" role="alert">
          <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
        </div>
      )}
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/product-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : productToEdit ? 'Actualizar Producto' : 'Registrar Producto'}
        </button>
      </div>
    </form>
  );
}


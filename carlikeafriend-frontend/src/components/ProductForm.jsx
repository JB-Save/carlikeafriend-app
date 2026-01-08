import { useProductForm } from '../hooks/useProductForm';
import { ImageUploadInput } from './ImageUploadInput';
import { ImagePreviewList } from './ImagePreviewList';
import { useNavigate } from 'react-router-dom';
import { API_CONFIG } from '../config/apiConfig';

// Componente del formulario para crear/editar productos
export const ProductForm = ({ productToEdit, onProductSaved }) => {
  const MAX_IMAGES = API_CONFIG.MAX_IMAGES_FOR_PRODUCTS;
  const URL = API_CONFIG.PRODUCT_IMAGES_BASE;
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    productData,
    newImages,
    existingImages,
    allCategories,
    allFeatures,
    isLoadingCategory,
    isLoadingFeature,
    categoryError,
    featureError,
    error,
    isLoading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleChange,
    handleCategoryCheckListChange,
    handleFeatureCheckListChange,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    handleSubmit,
  } = useProductForm(productToEdit, MAX_IMAGES, onProductSaved);

  return (
    <form onSubmit={handleSubmit} className="container">
      <div className="mb-3">
        <label htmlFor="name" className="form-label">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          value={productData.name}
          onChange={handleChange}
          className="form-control"
          required
          disabled={isLoading}
        />
      </div>
      <div className="mb-3">
        <label htmlFor="description" className="form-label">Descripción</label>
        <textarea
          id="description"
          name="description"
          value={productData.description}
          onChange={handleChange}
          rows="3"
          className="form-control"
          required
          disabled={isLoading}
        ></textarea>
      </div>
      <div className="mb-3">
        <label className="form-label">Categorías</label>
        <div className="border my-2">
          {categoryError &&
            (<div className="d-flex alert alert-danger text-center w-100">
              <p className="m-0"><strong>¡Error! </strong>{categoryError}</p>
            </div>)}
          {isLoadingCategory && <div className="text-center my-5"><div className="spinner-border text-primary" role="status"></div><p className="text-muted">Cargando categorías...</p></div>}
          <div className="d-flex justify-content-start align-items-center w-100 flex-wrap">
            {allCategories.map((category, index) => (
              <div className="form-check mx-2 my-2" key={index} style={{ width: '105px' }}>
                <input
                  type="checkbox"
                  id={`category-${index}`}
                  name="categories"
                  value={category.id}
                  className="form-check-input"
                  checked={productData.categories.some(val => val === category.id)}
                  onChange={handleCategoryCheckListChange}
                  disabled={isLoading}
                />
                <label className="form-check-label" htmlFor={`category-${index}`}>
                  {category.name}
                </label>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="mb-3">
        <label className="form-label">Características</label>
        <div className="border my-2">
          {featureError &&
            (<div className="d-flex alert alert-danger text-center w-100">
              <p className="m-0"><strong>¡Error! </strong>{featureError}</p>
            </div>)}
          {isLoadingFeature && <div className="text-center my-5"><div className="spinner-border text-primary" role="status"></div><p className="text-muted">Cargando características...</p></div>}
          <div className="d-flex justify-content-start align-items-center w-100 flex-wrap">
            {allFeatures.map((feature, index) => (
              <div className="form-check mx-2 my-2" key={index} style={{ width: '140px' }}>
                <input
                  type="checkbox"
                  id={`feature-${index}`}
                  name="features"
                  value={feature.id}
                  className="form-check-input"
                  checked={productData.features.some(val => val === feature.id)}
                  onChange={handleFeatureCheckListChange}
                  disabled={isLoading}
                />
                <label className="form-check-label" htmlFor={`feature-${index}`}>
                  {feature.name}
                </label>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="mb-3">
        <label htmlFor="price" className="form-label">Precio</label>
        <input
          type="number"
          id="price"
          name="price"
          value={productData.price}
          onChange={handleChange}
          step="0.01"
          className="form-control"
          required
          disabled={isLoading}
        />
      </div>

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

      {error && (
        <div className="alert alert-danger" role="alert">
          <strong>¡Error!</strong> {error}
        </div>
      )}
      <div className="d-flex justify-content-between">
        <button
          type="button"
          className="btn form-btn rounded-3"
          onClick={() => navigate("/administration/product-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3"
          disabled={isLoading}
        >
          {isLoading ? 'Guardando...' : productToEdit ? 'Actualizar Producto' : 'Crear Producto'}
        </button>
      </div>
    </form>
  );
}


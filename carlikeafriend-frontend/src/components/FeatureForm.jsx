import { useFeatureForm } from '../hooks/useFeatureForm';
import { ImageUploadInput } from './ImageUploadInput';
import { ImagePreviewList } from './ImagePreviewList';
import { useNavigate } from 'react-router-dom';
import { API_CONFIG } from '../config/apiConfig';

// Componente del formulario para crear/editar características
export const FeatureForm = ({ featureToEdit, onFeatureSaved }) => {
  const MAX_IMAGES = API_CONFIG.MAX_IMAGES_FOR_FEATURES;
  const URL = API_CONFIG.FEATURE_IMAGES_BASE;
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    featureData,
    newImages,
    existingImages,
    error,
    isLoading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleChange,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    handleSubmit,
  } = useFeatureForm(featureToEdit, MAX_IMAGES, onFeatureSaved);

  return (
    <form onSubmit={handleSubmit} className="container">
      <div className="mb-3">
        <label htmlFor="name" className="form-label">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          value={featureData.name}
          onChange={handleChange}
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
          onClick={() => navigate("/administration/feature-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3"
          disabled={isLoading}
        >
          {isLoading ? 'Guardando...' : featureToEdit ? 'Actualizar Característica' : 'Crear Característica'}
        </button>
      </div>
    </form>
  );
}


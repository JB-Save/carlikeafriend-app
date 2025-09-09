import { useProductForm } from '../hooks/useProductForm';
import { ImageUploadInput } from './ImageUploadInput';
import { ImagePreviewList } from './ImagePreviewList';
import { useNavigate } from 'react-router-dom';

// Componente del formulario para crear/editar productos
export const ProductForm = ({ productToEdit, onProductSaved }) => {
  const MAX_IMAGES = 5;
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    productData,
    newImages,
    existingImages,
    error,
    loading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleChange,
    handleNewImageChange,
    handleRemoveExistingImage,
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
          disabled={loading}
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
          disabled={loading}
        ></textarea>
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
          disabled={loading}
        />
      </div>

      <ImagePreviewList
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
      />

      {error && (
        <div className="alert alert-danger" role="alert">
          <strong>¡Error!</strong> {error}
        </div>
      )}
      <div className="d-flex justify-content-between">
        <button
          type="button"
          className="btn form-btn rounded-lg"
          onClick={() => navigate("/administration")}
          disabled={loading}
        >
          Volver al Panel
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-lg"
          disabled={loading}
        >
          {loading ? 'Guardando...' : productToEdit ? 'Actualizar Producto' : 'Crear Producto'}
        </button>
      </div>
    </form>
  );
}



// Componente para mostrar las imágenes existentes en el servidor y las nuevas
export const ImagePreviewList = ({ URL, images, onRemoveImage }) => {
  if (!images || images.length === 0) {
    return null;
  }

  const imageUrl = (image) => {
    return image?.imagePath
      ? `${URL}${image.imagePath}`
      : 'https://placehold.co/70x60/E0F2FE/3B82F6?text=No+Imagen';
  };

  // Manejar errores de imágen
  const handleImageError = (e) => {
    e.target.onerror = null; // Prevenir loop infinito
    e.target.src = 'https://placehold.co/70x60/E0F2FE/3B82F6?text=Imagen+No+Disponible';
  };

  return (
    <div className="mb-4">
      <h3 className="fs-6 fw-semibold">Imágenes actuales:</h3>
      <div className="row g-2">
        {images.map((image) => (
          <div key={image.id} className="col-4 col-md-3 col-lg-2">
            <div className="position-relative">
              <img
                src={imageUrl(image)}
                alt={`Previsualización imagen-${image.id}`}
                className="img-fluid rounded shadow-sm"
                onError={handleImageError}
              />
              <button
                type="button"
                onClick={() => onRemoveImage(image.id)}
                className="btn btn-danger rounded-circle position-absolute top-0 end-0 m-1 p-0 d-flex align-items-center justify-content-center"
                aria-label="Eliminar imagen"
                style={{ width: '20px', height: '20px' }}>
                <i className="bi bi-x-circle" style={{ fontSize: '12px' }}></i>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
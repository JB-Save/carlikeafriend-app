
// Componente para mostrar las imágenes existentes en el servidor y las nuevas
export const ImagePreviewList = ({ images, onRemoveImage }) => {
  if (!images || images.length === 0) {
    return null;
  }

  return (
    <div className="mb-4">
      <h3 className="fs-6 fw-semibold">Imágenes actuales:</h3>
      <div className="row g-2">
        {images.map((image) => (
          <div key={image.id} className="col-4 col-md-3 col-lg-2">
            <div className="position-relative">
              <img
                src={image.imagePath ? `http://localhost:8080/carlikeafriend/products/images${image.imagePath}` : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible'}
                alt={"Previsualización del producto"}
                className="img-fluid rounded shadow-sm"
              />
              <button
                type="button"
                onClick={() => onRemoveImage(image.id)}
                className="btn btn-danger btn-sm rounded-circle position-absolute top-0 end-0 m-1"
                aria-label="Eliminar imagen"><i className="bi bi-x-circle"></i>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
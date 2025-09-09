

export const ImageUploadInput = ({
  maxImages,
  availableSlots,
  newImages,
  onFileChange,
  fileInputRef,
  uploadError,
  canAddMoreImages}) => {
  return (
    <div className="mb-3">
      <label htmlFor="image-upload" className="form-label">
        Imágenes Nuevas del Producto ({newImages.length})
      </label>
      <input
        type="file"
        id="image-upload"
        accept="image/jpeg,image/png,image/gif,image/webp"
        onChange={onFileChange}
        ref={fileInputRef}
        multiple
        disabled={!canAddMoreImages}
        className="form-control"
      />
      {!canAddMoreImages && (
        <p className="text-danger mt-1">
          Has alcanzado el límite de {maxImages} imágenes.
        </p>
      )}
      {uploadError && (
        <p className="text-danger mt-1">{uploadError}</p>
      )}
      <div className="form-text mt-1">
        Selecciona hasta {availableSlots} imagen(es) más para {maxImages} en total. Tipos permitidos: JPG, PNG, GIF, WEBP.
      </div>
      {/* Sección para mostrar los nombres de los archivos seleccionados */}
      {newImages.length > 0 && (
        <div className="mt-2 form-text">
          <p className="fw-bold mb-1 fs-6">Archivos seleccionados:</p>
          <ul className="list-disc pl-5 text-sm">
            {newImages.map((file, index) => (
              <li key={index}>
                {file.name}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

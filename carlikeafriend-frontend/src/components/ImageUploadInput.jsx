

export const ImageUploadInput = ({
  maxImages,
  availableSlots,
  newImages,
  onFileChange,
  fileInputRef,
  uploadError,
  canAddMoreImages,
  onDeleteUploadedFile
}) => {
  return (
    <div className="mb-5">
      <label htmlFor="image-upload" className="form-label">
        Imágenes nuevas seleccionadas: ({newImages.length})
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
        maxImages === 1 ?
          <p className="text-danger mt-1">
            Has alcanzado el límite de {maxImages} imagen.
          </p>
          :
          <p className="text-danger mt-1">
            Has alcanzado el límite de {maxImages} imágenes.
          </p>
      )}
      {uploadError && (
        <p className="text-danger mt-1">{uploadError}</p>
      )}
      <div className="form-text mt-1">
        {availableSlots === 1 ?
          <p>Falta {availableSlots} imagen más para {maxImages} en total. Tipos permitidos: JPG, PNG, GIF, WEBP.</p>
          :
          <p>Faltan {availableSlots} imágenes más para {maxImages} en total. Tipos permitidos: JPG, PNG, GIF, WEBP.</p>
        }
      </div>
      {/* Sección para mostrar los nombres de los archivos seleccionados */}
      {newImages.length > 0 && (
        <div className="mt-2 form-text">
          <p className="fw-bold mb-1 fs-6">Archivos seleccionados:</p>
          <table className="table form-text">
            <thead>
              <tr>
                <th scope="col">#</th>
                <th scope="col">nombre</th>
                <th scope="col" className="text-center">Quitar</th>
              </tr>
            </thead>
            <tbody>
              {
                newImages.map((file, index) => (

                  <tr key={index}>
                    <th scope="row">{index + 1}</th>
                    <td>{file.name}</td>
                    <td className="d-flex justify-content-center align-items-center">
                      <button
                        type="button"
                        onClick={() => onDeleteUploadedFile(file)}
                        className="btn btn-danger rounded-circle m-0 p-0 d-flex align-items-center justify-content-center"
                        aria-label="Quitar archivo"
                        style={{ width: '20px', height: '20px' }}>
                        <i className="bi bi-x-circle" style={{ fontSize: '12px' }}></i>
                      </button>
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

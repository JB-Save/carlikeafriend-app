import { CompleteGalleryModal } from "./CompleteGalleryModal"

export const ImageGalleryModal = ({product, onClose}) => {

     if (!product) {
    //Si no hay producto no renderiza el modal
    return null;
  }

   return (
        <div className="modal fade show" style={{display: 'block'}} tabIndex="-1" role="dialog" aria-labelledby="imageGalleryModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-xl modal-dialog-centered" role="document">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="imageGalleryModalLabel">
                            Galería de Imágenes de {product.name}
                        </h5>
                        <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
                    </div>
                    <div className="modal-body">
                        {/* Se pasa la lista de imágenes al componente hijo */}
                        <CompleteGalleryModal images={product.images} productName={product.name} />
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn back-btn" onClick={onClose}>Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
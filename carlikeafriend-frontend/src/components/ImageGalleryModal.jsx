import { CompleteGalleryModal } from "./CompleteGalleryModal"

export const ImageGalleryModal = ({ product, onClose }) => {

    if (!product) {
        //Si no hay producto no renderiza el modal
        return null;
    }

    return (
        <div className="modal fade show" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1060 }} tabIndex="-1" role="dialog" aria-labelledby="imageGalleryModalLabel" aria-modal="true">
            <div className="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable" role="document">
                <div className="modal-content">
                    <div className="modal-header bg-light">
                        <h5 className="modal-title fs-5 fs-md-4 text-truncate w-100" id="imageGalleryModalLabel">
                            Galería de Imágenes: <span className="fw-normal">{product.name}</span>
                        </h5>
                        <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
                    </div>
                    <div className="modal-body p-3 p-md-4">
                        <CompleteGalleryModal images={product.productImages} productName={product.name} />
                    </div>
                    <div className="modal-footer bg-light">
                        <button type="button" className="btn back-btn w-100 w-sm-auto" onClick={onClose}>Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
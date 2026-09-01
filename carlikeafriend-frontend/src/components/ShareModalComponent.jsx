import { useState } from "react";
import { useSocialShare } from "../hooks/useSocialShare";
import { API_CONFIG } from "../config/apiConfig";
import { UserContext } from "../context/UserContext";

export const ShareModalComponent = ({ product, onClose }) => {

    const { shareToNetwork, isSharing } = useSocialShare(product.id);
    const [message, setMessage] = useState(`¡Mira este increíble ${product.name} que encontré para rentar!`);

    // URL real que se va a compartir
    const productUrl = `${window.location.origin}/product-detail/${product.id}`;

    const handleShare = (platform) => {
        shareToNetwork(platform, message, productUrl);
        if (platform === 'copy') {
            alert('¡Enlace copiado al portapapeles!');
        }
        onClose();
    };

    const IMAGE_URL = product.productImages && product.productImages.length > 0
        ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${product.productImages[0].imagePath}`
        : 'https://placehold.co/60x60/E0F2FE/3B82F6?text=No+Imagen';

    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/60x60/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    return (
        <div className="modal-backdrop" style={{ backgroundColor: 'rgba(0,0,0,0.5)', position: 'fixed', inset: 0, zIndex: 1050, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div className="bg-white p-4 rounded-4 shadow-lg w-100 max-w-md" style={{ maxWidth: '400px' }}>
                <div className="d-flex justify-content-between border-bottom pb-2 mb-3">
                    <h5 className="fw-bold mb-0">Compartir Vehículo</h5>
                    <button onClick={onClose} className="btn-close" aria-label="Close"></button>
                </div>

                <div className="d-flex align-items-center mb-3 p-2 bg-light rounded-3">
                    <img
                        src={IMAGE_URL}
                        alt={product.name}
                        onError={handleImageError}
                        className="rounded"
                        style={{ width: '60px', height: '60px', objectFit: 'cover' }}
                    />
                    <div className="ms-3">
                        <h6 className="mb-0 fw-bold">{product.name}</h6>
                        <small className="text-muted line-clamp-2">{product.description.substring(0, 50)}...</small>
                    </div>
                </div>

                <div className="mb-3">
                    <label className="form-label small fw-semibold">Personaliza tu mensaje (Opcional):</label>
                    <textarea
                        className="form-control"
                        rows="2"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Escribe algo sobre este vehículo..."
                    />
                    <small className="text-muted" style={{ fontSize: '0.7rem' }}>*Facebook y LinkedIn no admiten mensajes pre-escritos.</small>
                </div>

                <div className="d-flex flex-wrap justify-content-center gap-3 mt-4">
                    <button onClick={() => handleShare('whatsapp')} disabled={isSharing} className="btn btn-success rounded-circle d-flex" title="WhatsApp">
                        <i className="bi bi-whatsapp"></i>
                    </button>
                    <button onClick={() => handleShare('telegram')} disabled={isSharing} className="btn btn-info rounded-circle d-flex text-white" title="Telegram">
                        <i className="bi bi-telegram"></i>
                    </button>
                    <button onClick={() => handleShare('facebook')} disabled={isSharing} className="btn btn-primary rounded-circle d-flex" title="Facebook">
                        <i className="bi bi-facebook"></i>
                    </button>
                    <button onClick={() => handleShare('twitter')} disabled={isSharing} className="btn btn-dark rounded-circle d-flex" title="X (Twitter)">
                        <i className="bi bi-twitter-x"></i>
                    </button>
                    <button onClick={() => handleShare('linkedin')} disabled={isSharing} className="btn btn-primary rounded-circle d-flex" style={{ backgroundColor: '#0a66c2', border: 'none' }} title="LinkedIn">
                        <i className="bi bi-linkedin"></i>
                    </button>
                    <button onClick={() => handleShare('copy')} disabled={isSharing} className="btn btn-secondary rounded-circle d-flex" title="Copiar Enlace">
                        <i className="bi bi-link-45deg"></i>
                    </button>
                </div>
            </div>
        </div>
    );
}

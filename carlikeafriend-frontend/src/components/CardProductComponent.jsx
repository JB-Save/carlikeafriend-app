import { Link } from "react-router-dom";
import { useCurrencyFormatter } from "../hooks/useCurrencyFormatter";
import { API_CONFIG } from "../config/apiConfig";

export const CardProductComponent = ({ product }) => {

    const { formatCurrency } = useCurrencyFormatter();

    const IMAGE_URL = product.productImages && product.productImages.length > 0
        ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${product.productImages[0].imagePath}`
        : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';

    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    return (
        <div className="col">
            <div className="card card-shadow card-background rounded-3 overflow-hidden h-100">
                <img
                    src={IMAGE_URL}
                    className="card-img-top"
                    alt={product.name}
                    onError={handleImageError}
                    loading="lazy"
                />
                <div className="card-body d-flex flex-column">
                    <h3 className="card-title h5 fw-semibold mb-2">{product.name}</h3>
                    {/* <p className="card-text mb-3 flex-grow-1">{product.description}</p> */}
                    <div className="d-flex flex-column flex-md-row flex-wrap gap-2 mt-auto align-items-center">
                        <div className="flex-fill text-start w-100 w-md-auto">
                            <span className="fs-5 fw-bold text-primary">{formatCurrency(product.price)}</span> {/*${parseFloat(product.price).toFixed(2)}*/}
                            <small className="text-muted"> /día</small>
                        </div>
                        <div className="flex-fill text-md-end text-center w-100 w-md-auto">
                            <Link to={`/product-detail/${product.id}`}
                                className="btn detail-btn w-100 rounded-3"
                            >
                                Ver Detalle
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}




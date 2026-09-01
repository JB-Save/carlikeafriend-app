import { Link } from "react-router-dom";
import { useCurrencyFormatter } from "../hooks/useCurrencyFormatter";
import { API_CONFIG } from "../config/apiConfig";
import { useContext, useState } from "react";
import { FavoriteContext } from "../context/FavoriteContext";
import { useBooking } from "../context/BookingContext";
import { usePricing } from "../hooks/usePricing";
import '../styles/ProductCardStyle.css'

export const ProductCardComponent = ({ product }) => {

    const { isProductFavorite, toggleFavorite } = useContext(FavoriteContext);
    const { bookingData } = useBooking();
    const [isProcessing, setIsProcessing] = useState(false);
    const { formatCurrency } = useCurrencyFormatter();

    // Consumimos el motor de precios (Seguro Básico por defecto para la vista previa)
    const { pricingDetails, isLoadingPricing } = usePricing(product, bookingData, [], 'BASIC');


    const IMAGE_URL = product.productImages && product.productImages.length > 0
        ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${product.productImages[0].imagePath}`
        : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';

    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    // Todas las instancias de este producto en la pantalla leerán el mismo valor
    const isFavorite = isProductFavorite(product.id);

    const handleToggleFavorite = async (e) => {
        e.preventDefault();
        setIsProcessing(true);
        await toggleFavorite(product);
        setIsProcessing(false);
    };

    return (
        <div className="col">
            <div className="card border-0 h-100 shadow-sm rounded-4 overflow-hidden product-card-hover bg-white">
                <div className="position-relative overflow-hidden bg-light" style={{ height: '220px' }}>
                    {/* Botón de Favorito */}
                    <button
                        onClick={handleToggleFavorite}
                        disabled={isProcessing}
                        className="btn position-absolute top-0 end-0 m-3 p-0 bg-white rounded-circle shadow-sm z-2 d-flex align-items-center justify-content-center"
                        style={{ width: '35px', height: '35px', opacity: 0.9 }}
                    >
                        {/* El icono cambiará instantáneamente en TODAS las tarjetas de este producto */}
                        <i className={`bi ${isFavorite ? 'bi-heart-fill text-danger' : 'bi-heart produc-card-text-muted'} lh-1`}
                            style={{ fontSize: '1.2rem', transform: 'translateY(2px)' }}
                        ></i>
                    </button>
                    <img
                        src={IMAGE_URL}
                        className="w-100 h-100 object-fit-cover product-image"
                        alt={product.name}
                        onError={handleImageError}
                        loading="lazy"
                    />
                    <div className="position-absolute top-0 start-0 m-3">
                        <span className="badge rounded-pill px-3 py-2 badge-custom">
                            Disponible
                        </span>
                    </div>
                </div>

                <div className="card-body d-flex flex-column p-4">
                    <h4 className="fw-bold mb-3 title-color m-0">{product.name}</h4>
                    {/* Mostrar promedio de rating y total de reseñas */}
                    <div className="d-flex align-items-center mb-2">
                        <div className="d-flex align-items-center bg-warning-subtle px-2 py-1 rounded-pill me-2">
                            <i className="bi bi-star-fill text-warning me-1" style={{ fontSize: '0.85rem' }}></i>
                            <span className="fw-bold text-warning-emphasis small">
                                {product.averageRating?.toFixed(1) || "0.0"}
                            </span>
                        </div>
                        {/* Separador y Valoraciones */}
                        <div className="ps-2 border-start produc-card-text-muted" style={{ fontSize: '0.78rem', lineHeight: '1' }}>
                            {product.totalReviews || 0} <span className="d-none d-sm-inline">Reviews</span>
                        </div>
                    </div>

                    <div className="d-flex flex-column flex-md-row flex-wrap gap-3 mt-auto align-items-center pt-3 border-top" style={{ borderColor: '#F4F3F2' }}>

                        <div className="flex-fill text-start w-100 w-md-auto">
                            {isLoadingPricing || !pricingDetails ? (
                                <div className="spinner-border spinner-border-sm" role="status"></div>
                            ) : (
                                <>
                                    {/* RENDERIZADO DINÁMICO DE PRECIO */}
                                    <span className="fs-4 d-block fw-bold price-color lh-1">
                                        {pricingDetails.hasDates ? formatCurrency(pricingDetails.total) : formatCurrency(product.price)}
                                    </span>
                                    <small className="fw-medium text-uppercase produc-card-text-muted" style={{ fontSize: '0.75rem' }}>
                                        {pricingDetails.hasDates
                                            ? `Total por ${pricingDetails.rentalDays} día(s)`
                                            : 'Desde / día'}
                                    </small>
                                </>
                            )}
                        </div>

                        <div className="flex-fill text-md-end text-center w-100 w-md-auto">
                            <Link to={`/product-detail/${product.id}`}
                                className="btn fw-bold px-4 py-2 w-100 rounded-pill detail-btn detail-btn-hover"
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




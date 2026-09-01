import React from 'react';
import { Link } from 'react-router-dom';
import { useCurrencyFormatter } from '../hooks/useCurrencyFormatter';
import { API_CONFIG } from '../config/apiConfig';
import { SPECIAL_FEATURES } from '../utils/featureConstants';

export const FavoriteCardComponent = ({ product, onRemove }) => {

    const { formatCurrency } = useCurrencyFormatter();

    const IMAGE_URL = product.productImages && product.productImages.length > 0
        ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${product.productImages[0].imagePath}`
        : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';

    const iconPath = (feature) => {
        return feature?.icon?.imagePath
            ? `${API_CONFIG.FEATURE_IMAGES_BASE}${feature.icon.imagePath}`
            : 'https://placehold.co/18x4/E0F2FE/3B82F6?text=?';
    };

    // Manejar errores de imágen del producto
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    // Manejar errores del ícono
    const handleIconError = (e) => {
        e.target.onerror = null;
        e.target.src = 'https://placehold.co/18x4/E0F2FE/3B82F6?text=:(';
    };

    return (
        <div className="card h-100 shadow-sm border-0 position-relative hover-lift overflow-hidden">

            <div className="d-flex align-items-center m-2">
                <div className="d-flex align-items-center bg-warning-subtle px-2 py-1 rounded-pill me-2">
                    <i className="bi bi-star-fill text-warning me-1" style={{ fontSize: '0.85rem' }}></i>
                    <span className="fw-bold text-warning-emphasis small">
                        {product.averageRating?.toFixed(1) || "0.0"}
                    </span>
                </div>
                {/* Separador y Valoraciones */}
                <div className="ps-2 border-start favorite-block-text-muted" style={{ fontSize: '0.78rem', lineHeight: '1' }}>
                    {product.totalReviews || 0} <span className="d-none d-sm-inline">Reviews</span>
                </div>
            </div>

            <button
                onClick={(e) => { e.preventDefault(); onRemove(product.id); }}
                className="btn btn-white btn-sm position-absolute top-0 end-0 m-2 rounded-circle shadow z-3"
                style={{ width: '35px', height: '35px' }}
            >
                <i className="bi bi-x-lg text-danger"></i>
            </button>

            <div className="position-relative bg-light" style={{ height: '160px' }}>
                <img
                    src={IMAGE_URL}
                    className="w-100 h-100 object-fit-cover"
                    alt={product.name}
                    onError={handleImageError}
                    loading="lazy"
                />
            </div>

            <div className="card-body d-flex flex-column">
                <small className="favorite-block-text-primary fw-bold text-uppercase" style={{ fontSize: '0.7rem' }}>
                    {product.make?.name || 'Vehículo'}
                </small>
                <h5 className="favorite-block-title-color fw-bold text-truncate">{product.name}</h5>

                <div className="d-flex justify-content-between py-2 my-2 border-top border-bottom bg-light rounded px-2">
                    {/* PASAJEROS */}
                    {product.features?.filter(f => SPECIAL_FEATURES.PASAJERO.includes(f.name.toLowerCase())).map(f => (
                        <div key={f.id} className="text-center small px-2">
                            <img
                                src={iconPath(f)}
                                alt={f.name}
                                onError={handleIconError}
                                style={{ width: '18px', marginRight: '4px' }} />
                            <span>{product.passengerCapacity || '4'}</span>
                        </div>
                    ))}

                    {/* PUERTAS */}
                    {product.features?.filter(f => SPECIAL_FEATURES.PUERTA.includes(f.name.toLowerCase())).map(f => (
                        <div key={f.id} className="text-center small px-2">
                            <img
                                src={iconPath(f)}
                                alt={f.name}
                                onError={handleIconError}
                                style={{ width: '18px', marginRight: '4px' }} />
                            <span>{product.numberOfDoors || '4'}</span>
                        </div>
                    ))}

                    {/* MALETAS */}
                    {product.features?.filter(f => SPECIAL_FEATURES.EQUIPAJE.includes(f.name.toLowerCase())).map(f => (
                        <div key={f.id} className="text-center small px-2">
                            <img
                                src={iconPath(f)}
                                alt={f.name}
                                onError={handleIconError}
                                style={{ width: '18px', marginRight: '4px' }} />
                            <span>{product.baggageCapacity || '2'}</span>
                        </div>
                    ))}

                    {/* TRANSMISIÓN*/}
                    {product.features?.filter(f => f.name.toLowerCase().includes('mecánica') || f.name.toLowerCase().includes('automática')).map(f => (
                        <div key={f.id} className="text-center small px-2">
                            <img
                                src={iconPath(f)}
                                alt={f.name}
                                onError={handleIconError}
                                style={{ width: '18px', marginRight: '4px' }} />
                            <span>{f.name.toLowerCase().includes('mecánica') ? 'M' : 'A'}</span>
                        </div>
                    ))}
                </div>

                <div className="mt-auto d-flex flex-column gap-3 w-100 align-items-stretch">
                    <div className="d-flex align-items-baseline justify-content-between flex-wrap gap-1">
                        <span className="h5 mb-0 favorite-block-text-primary fw-bold">
                            {formatCurrency(product.price)}
                        </span>
                        <small className="favorite-block-text-muted text-break">
                            Desde / día
                        </small>
                    </div>

                    <Link to={`/product-detail/${product.id}`} className="btn favorite-block-btn-detail text-center rounded-pill w-100 px-3 py-2">
                        Ver Detalle
                    </Link>
                </div>
            </div>
        </div>
    );
}
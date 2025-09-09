import { Link } from "react-router-dom";

export const CardProductComponent = ({ product }) => {

    const imageUrl = product.images && product.images.length > 0
        ? `http://localhost:8080/carlikeafriend/products/images${product.images[0].imagePath}`
        : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';


    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    return (
        <div className="col">
            <div className="card card-shadow card-background rounded-lg overflow-hidden h-100">
                <img
                    src={imageUrl}
                    className="card-img-top"
                    alt={product.name}
                    onError={handleImageError}
                />
                <div className="card-body d-flex flex-column">
                    <h3 className="card-title h5 fw-semibold text-gray-800 mb-2">{product.name}</h3>
                    <p className="card-text text-gray-600 mb-3 flex-grow-1">{product.description}</p>
                    <div className="d-flex flex-column flex-md-row flex-wrap gap-1 mt-auto">
                         <div className="flex-fill text-start">
                        <span className="fs-5 fw-bold text-primary">${parseFloat(product.price).toFixed(2)}/día</span>
                        </div>
                        <div className="flex-fill text-md-end text-center">
                        <Link to={`/product-detail/${product.id}`}
                            className="btn detail-btn rounded-lg"
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




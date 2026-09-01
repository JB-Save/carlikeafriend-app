import { useEffect, useState } from "react";
import { useMessageModal } from "../context/MessageModalContext"
import { useFetch } from "../hooks/useFetch";
import { useNavigate } from "react-router-dom";
import { API_CONFIG } from "../config/apiConfig";

export const CategoriesSection = () => {

    const navigate = useNavigate();
    const { setModalMessage } = useMessageModal();
    const [allCategoryProducts, setAllCategoryProducts] = useState([]);
    const [err, setErr] = useState(null);

    const URL = API_CONFIG.CATEGORIES;
    const { data, isLoading, error, fetchData } = useFetch();

    const sentToFilter = (categoryId) => {
        navigate(`/product-filter?categories=${categoryId}`, { replace: true });
    };

    useEffect(() => {
        fetchData(URL, 'GET');
    }, [fetchData, URL]);

    useEffect(() => {
        if (data) {
            setAllCategoryProducts(data);
        }

        if (error) {
            console.error("Error al obtener categorías: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setErr(message || "Ocurrio un error inesperado.");
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage]);

    const handleImageError = (e) => {
        e.target.onerror = null;
        e.target.src = 'https://placehold.co/60x60/60A5FA/FFFFFF?text=Imagen+No+Disponible';
    };

    return (
        <section className="container mb-5 p-3 rounded-4 category-section-bg">
            <div className="d-flex align-items-center mb-4 border-start border-4 ps-3 category-border">
                <h3 className="fw-bold mb-0 title-color">Explora por Categorías</h3>
            </div>

            {err && <div className="alert alert-danger text-center rounded-3 shadow-sm">{err}</div>}

            <div className="category-listings-section mt-2">
                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border" style={{ width: '3rem', height: '3rem' }} role="status"></div>
                        <p className="mt-2 text-muted-custom fw-medium">Cargando categorías...</p>
                    </div>
                ) : (!allCategoryProducts || allCategoryProducts.length === 0) ? (
                    <div className="text-center text-muted-custom p-5 bg-light rounded-4 shadow-sm border border-white">
                        <i className="bi bi-speedometer2 fs-2 text-muted-custom mb-2 d-block opacity-50"></i>
                        No hay categorías disponibles.
                    </div>
                ) : (
                    <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-4">
                        {allCategoryProducts.map((category) => {

                            // Lógica real de recuperación de imagen de categoría
                            const IMAGE_URL = category?.categoryImage?.imagePath
                                ? `${API_CONFIG.CATEGORY_IMAGES_BASE}${category.categoryImage.imagePath}`
                                : 'https://placehold.co/60x60/60A5FA/FFFFFF?text=No+Imagen';

                            return (
                                <div key={category.id} className="col">
                                    <div
                                        className="card h-100 border-0 shadow-sm text-center p-3 rounded-4 category-card-hover"
                                        onClick={() => sentToFilter(category.id)}
                                    >
                                        <div className="p-3 bg-light rounded-circle mx-auto mb-3 d-flex align-items-center justify-content-center category-icon-wrapper">
                                            <img
                                                src={IMAGE_URL}
                                                alt={category.name}
                                                onError={handleImageError}
                                                className="w-100 h-100 object-fit-contain"
                                                loading="lazy"
                                            />
                                        </div>
                                        <h6 className="fw-bold mb-0 title-color">{category.name}</h6>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </section>
    )
}
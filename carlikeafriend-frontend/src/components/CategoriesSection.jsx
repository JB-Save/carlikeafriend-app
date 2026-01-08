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

    //Creamos una función que recibe la característica como argumento
    const sentToFilter = (categoryId) => {
        // Navega a la ruta '/product-filter' y pasa el objeto 'category' como estado
        navigate('/product-filter', { replace: true, state: { filterCategoryId: categoryId } });
    };

    // Este useEffect solo debe ejecutarse una vez al montar el componente.
    useEffect(() => {
        fetchData(URL, 'GET');
    }, []);

    // Este useEffect procesa los datos una vez que han sido recibidos.
    useEffect(() => {
        if (data) {
            //Establece el estado con la lista completa
            setAllCategoryProducts(data);
        }

        if (error) {
            console.error(error);
            setErr(error.message || "Ocurrio un error inesperado.");
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage]);

    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/60x60/60A5FA/FFFFFF?text=Imagen+No+Disponible';
    };

    return (

        <section className="container p-4 rounded-3 shadow-sm mb-5">
            <h3 className="fw-bold category-text mb-3">Explora por Categorías</h3>

            {err && <div className="alert alert-danger text-center">{err}</div>}

            <div className="category-listings-section my-5">

                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border text-primary" role="status"></div>
                        <p className="mt-2 text-muted">Cargando categorías...</p>
                    </div>
                ) : (!allCategoryProducts || allCategoryProducts.length === 0) ? (
                    <div className="text-center text-muted">No hay categorías disponibles.</div>
                ) : (
                    <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                        {allCategoryProducts.map((category) => {

                            const IMAGE_URL = category?.categoryImage?.imagePath
                                ? `${API_CONFIG.CATEGORY_IMAGES_BASE}${category.categoryImage.imagePath}`
                                : 'https://placehold.co/60x60/60A5FA/FFFFFF?text=No+Imagen';

                            return (

                                <div key={category.id} className="col">
                                    <div
                                        className="card card-categories-home cursor-pointer text-center h-100 card-shadow card-background rounded-3"
                                        onClick={() => sentToFilter(category.id)}
                                    >
                                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                                            <img
                                                src={IMAGE_URL}
                                                alt={category.name}
                                                onError={handleImageError}
                                                className="mb-2 w-50"
                                                loading="lazy" // Optimización: carga perezosa
                                            />
                                            <h5 className="card-title mb-0">{category.name}</h5>
                                        </div>
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

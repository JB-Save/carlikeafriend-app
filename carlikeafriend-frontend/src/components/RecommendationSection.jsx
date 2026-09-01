import { useEffect, useMemo, useState } from "react"
import { useFetch } from "../hooks/useFetch";
import { ProductCardComponent } from "./ProductCardComponent";
import { PaginationControlsComponent } from "./PaginationControlsComponent";
import { useMessageModal } from "../context/MessageModalContext";
import { API_CONFIG } from "../config/apiConfig";

export const RecommendationSection = ({ productsPerPage, type }) => {

    const { setModalMessage } = useMessageModal();
    const [allProducts, setAllProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [err, setErr] = useState(null);

    const URL = API_CONFIG.RECOMMENDED_PRODUCTS;
    const { data, isLoading, error, fetchData } = useFetch();

    useEffect(() => {
        fetchData(URL, 'GET')
    }, [])

    useEffect(() => {
        if (data) {
            setAllProducts(data);
            setCurrentPage(1);
        }
        if (error) {
            console.error("Error al obtener recomendaciones: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setErr(message || "Ocurrio un error inesperado.");
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage]);

    const totalPages = Math.ceil(allProducts.length / productsPerPage);
    const currentProductsDisplay = useMemo(() => {
        const startIndex = (currentPage - 1) * productsPerPage;
        return allProducts.slice(startIndex, startIndex + productsPerPage);
    }, [allProducts, currentPage, productsPerPage]);

    return (
        <section className="container mb-5 py-3">

            <div className="text-center mb-5">
                <span className="badge px-3 py-2 mb-2 rounded-pill shadow-sm" style={{ backgroundColor: '#70ACDE', color: '#2E2E84' }}>Top Selección</span>
                <h3 className="fw-bold display-6 title-color">Nuestras Recomendaciones</h3>
                <div className="mx-auto mt-2" style={{ width: '60px', height: '4px', backgroundColor: '#1F88E6', borderRadius: '2px' }}></div>
            </div>

            {err && <div className="alert alert-danger text-center rounded-3 shadow-sm">{err}</div>}

            <div className="product-listings-section">
                {isLoading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border" style={{ width: '3rem', height: '3rem' }} role="status"></div>
                        <p className="mt-2 fw-medium text-muted-custom">Preparando recomendaciones...</p>
                    </div>
                ) : (!currentProductsDisplay || currentProductsDisplay.length === 0) ? (
                    <div className="text-center text-muted-custom p-5 bg-light rounded-4 shadow-sm border border-white">
                        <i className="bi bi-emoji-frown fs-2 text-muted-custom mb-2 d-block opacity-50"></i>
                        No hay productos recomendados por el momento.
                    </div>
                ) : (
                    <div className="row row-cols-1 row-cols-md-2 g-4">
                        {
                            currentProductsDisplay.map(product => (
                                <ProductCardComponent key={`rec-${product.id}`} product={product} />
                            ))
                        }
                    </div>
                )}

                {allProducts.length > productsPerPage && (
                    <PaginationControlsComponent
                        currentPage={currentPage}
                        totalPages={totalPages}
                        goToPage={setCurrentPage}
                        type={type}
                    />
                )}
            </div>
        </section>
    )
}
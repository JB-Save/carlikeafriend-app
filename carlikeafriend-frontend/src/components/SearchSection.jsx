import { useState, useEffect } from 'react';
import { useFetch } from '../hooks/useFetch';
import { PaginationControlsComponent } from './PaginationControlsComponent';
import { CardProductComponent } from './CardProductComponent'
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const SearchSection = ({ productsPerPage, type }) => {

    const { setModalMessage } = useMessageModal();
    const [allProducts, setAllProducts] = useState([]);
    const [currentProducts, setCurrentProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [err, setErr] = useState(null);
    const totalPages = Math.ceil(allProducts.length / productsPerPage);

    const URL = API_CONFIG.PRODUCTS;

    const { data, isLoading, error, fetchData } = useFetch();

    // Este useEffect solo debe ejecutarse una vez al montar el componente.
    useEffect(() => {
        fetchData(URL, 'GET');
    }, []);

    // Este useEffect procesa los datos una vez que han sido recibidos.
    useEffect(() => {
        if (data) {
            const fetchedProducts = data;
            // 1. Ordena los productos de forma aleatoria una sola vez
            const shuffledProducts = fetchedProducts.sort(() => 0.5 - Math.random());
            // 2. Establece el estado con la lista completa y aleatoria de productos
            setAllProducts(shuffledProducts);
        }

        if (error) {
            console.error(error);
            setErr(error.message || "Ocurrio un error inesperado.");
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage]);

    // Este useEffect maneja la paginación. Se ejecuta solo cuando allProducts o currentPage cambian.
    useEffect(() => {
        const startIndex = (currentPage - 1) * productsPerPage;
        const endIndex = startIndex + productsPerPage;
        const productsToDisplay = allProducts.slice(startIndex, endIndex);
        setCurrentProducts(productsToDisplay);
    }, [allProducts, currentPage, productsPerPage]);

    const goToPage = (page) => {
        let newPage = page;
        if (newPage < 1) newPage = 1;
        if (newPage > totalPages && totalPages > 0) newPage = totalPages;
        if (totalPages === 0) newPage = 1;
        setCurrentPage(newPage);
    };

    return (
        <section className="container search-text p-4 rounded-3 shadow-sm mb-5"> {/* Sección de Buscador */}
            <h3 className="fw-bold search-text mb-3 text-center">Encuentra tu Auto Ideal</h3>
            <form className="row search-form g-3">

                <div className="col-12 col-lg-4">
                    <label htmlFor="pickupCity" className="form-label">Ciudad de Recogida</label>
                    <input type="text" className="form-control" id="pickupCity" placeholder="Ciudad o dirección" />
                </div>

                <div className="col-12 col-lg-3">
                    <label htmlFor="pickupDate" className="form-label">Fecha y Hora de Recogida</label>
                    <div className="row g-2">
                        <div className="col">
                            <input type="date" className="form-control" id="pickupDate" />
                        </div>
                        <div className="col">
                            <input type="time" className="form-control" id="pickupTime" step="900" />
                        </div>
                    </div>
                </div>

                <div className="col-12 col-lg-3">
                    <label htmlFor="returnDate" className="form-label">Fecha y Hora de Retorno</label>
                    <div className="row g-2">
                        <div className="col">
                            <input type="date" className="form-control" id="returnDate" />
                        </div>
                        <div className="col">
                            <input type="time" className="form-control" id="returnTime" step="900" />
                        </div>
                    </div>
                </div>

                <div className="col-12 col-lg-2 d-grid">
                    <label className="form-label">
                        &nbsp;
                    </label>
                    <button className="btn search-btn mb-4" type="button">Buscar</button>
                </div>
            </form>
            <hr className="my-4" />
            <h4 className="fw-bold search-text mb-3">Productos Aleatorios para el Buscador (Máx. 10 por página)</h4>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {/* Los productos para el buscador se cargarán aquí dinámicamente */}
            <div className="product-listings-section my-5">
                {isLoading ? (
                    <div className="text-center my-5">
                        <div className="spinner-border text-primary" role="status"></div>
                        <p className="mt-2 text-muted">Cargando productos...</p>
                    </div>
                ) : (!currentProducts || currentProducts.length === 0) ? (
                    <div className="text-center text-muted">No hay productos disponibles.</div>
                ) : (
                    <div className="row row-cols-1 row-cols-md-2 g-4">
                        {
                            currentProducts.map(product => (
                                <CardProductComponent key={product.id} product={product} />
                            ))
                        }
                    </div>
                )}
                {/* Controles de Paginación para búsqueda*/}
                <PaginationControlsComponent
                    currentPage={currentPage}
                    totalPages={totalPages}
                    goToPage={goToPage}
                    type={type}
                />
            </div>
        </section>
    )
}

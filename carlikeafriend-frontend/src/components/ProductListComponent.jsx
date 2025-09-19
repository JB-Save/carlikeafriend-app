import { useEffect, useState } from 'react'
import { useFetch } from '../hooks/useFetch';
import { ProductTableComponent } from './ProductTableComponent';
import { useMessageModal } from '../context/MessageModalContext';
import { DeleteConfirmationModalComponent } from './DeleteConfirmationModalComponent';

export const ProductListComponent = () => {

    const { setModalMessage } = useMessageModal(); // Hook para el mensaje
    const [allProducts, setAllProducts] = useState([]);
    const [err, setErr] = useState(null);
    const [errDelete, setErrDelete] = useState(null);
    const { data, isLoading, error, fetchData } = useFetch();
    const { data: deleteData, isLoading: isDelete, error: deleteDataError, fetchData: fetchDeleteData } = useFetch();
    const [productIdToDelete, setProductIdToDelete] = useState(null)
    const url = "http://localhost:8080/carlikeafriend/products";


    useEffect(() => {
        fetchData(url, 'GET');
    }, [])

    useEffect(() => {
        if (data) {
            setAllProducts(data);
        }

        if (error) {
            console.error("Error al cargar la lista de productos.", error);
            const errorMessage = "Error al cargar la lista de productos. Por favor, inténtalo de nuevo.";
            setErr(errorMessage);
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage])

    useEffect(() => {
        if (deleteData?.status === 204) {
            const successMessage = "Producto eliminado exitosamente.";
            setErrDelete(successMessage);
            fetchData(url, 'GET');            
        };

        if (deleteDataError) {
            console.error("Error al eliminar producto:", deleteDataError);
            const errorMessage = "Error al eliminar el producto.";
            setErrDelete(errorMessage);
            setModalMessage("Ocurrió un problema en la aplicación.");
        };
        const timer = setTimeout(() => {
            setErrDelete(null); // Oculta la alerta
        }, 3000); // 3 segundos
        return () => clearTimeout(timer); // Limpia el temporizador si el componente se desmonta
    }, [deleteData, deleteDataError, setModalMessage]);


    const deleteFunction = async (productIdToDelete) => {
        await fetchDeleteData(`${url}/${productIdToDelete}`, 'DELETE');
    };

    return (
        <div id="product-list-content" className="container-fluid py-2"> {/*Contenido principal de la lista de productos  */}
            <h2 className="h3 fw-bold text-product-list text-center mt-2">Lista de Productos Disponibles</h2>
            {err && <div className="alert alert-danger text-center">{err}</div>}
            {errDelete && <div className={`alert ${deleteDataError ? 'alert-danger' : 'alert-success'}  text-center fade ${errDelete ? 'show' : ''}`} style={{ transition: 'opacity 0.5s ease-in-out' }}>{errDelete}</div>}
            <div className="card card-shadow rounded-lg p-4">
                {isLoading && <div className="text-center my-5"><div className="spinner-border text-primary" role="status"></div><p>Cargando productos...</p></div>}
                {!isLoading && allProducts && allProducts.length > 0 &&
                    <ProductTableComponent products={allProducts} setProductIdToDelete={setProductIdToDelete} />
                }
                {!isLoading && (!allProducts || allProducts.length === 0) &&
                    <div className="text-center text-muted mb-3">No hay productos disponibles.</div>
                }
            </div>
            {/* El modal de confirmación se renderiza condicionalmente aquí */}
            {productIdToDelete && (
                <DeleteConfirmationModalComponent
                    id={productIdToDelete}
                    deleteFunction={deleteFunction}
                    onClose={() => setProductIdToDelete(null)}
                />
            )}
        </div>
    );
}

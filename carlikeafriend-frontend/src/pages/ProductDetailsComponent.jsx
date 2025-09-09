import { useState, useEffect } from "react";
import { ImageGalleryModal } from "../components/ImageGalleryModal";
import { useFetch } from "../hooks/useFetch";
import { useNavigate, useParams } from "react-router-dom";
import { useMessageModal } from "../context/MessageModalContext";
import "../styles/MainStyle.css";
import "../styles/ProductDetailStyle.css";

export const ProductDetailsComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para el mensaje
    const { id } = useParams(); // <-- Obtener el ID de la URL
    const [singleProduct, setSingleProduct] = useState(null)
    const [modalOpen, setModalOpen] = useState(false)
    
    const url = `http://localhost:8080/carlikeafriend/products/${id}`; // <-- Usar el ID para construir la URL
    const { data, isLoading, error, fetchData } = useFetch();
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    // Este useEffect se ejecuta cuando cambie el id.
    useEffect(() => {
        if (id) { // <-- Asegurarse de que el ID exista antes de llamar a la API
            fetchData(url, 'GET');
        }
    }, [id, url]);


    // Este useEffect procesa los datos una vez que han sido recibidos.
    useEffect(() => {
        if (data) {

            setSingleProduct(data);
        }

        if (error) {
            console.error("Error al cargar el producto: " + error);
            setModalMessage("Error al cargar el producto. Por favor, inténtalo de nuevo.");
        }
    }, [data, error, setModalMessage]);

    const imagePath = (item, index) => {
        const imageUrl = item?.images?.[index]?.imagePath || 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';
        return `http://localhost:8080/carlikeafriend/products/images${imageUrl}`;
    };


    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    const showModal = () => {
        setModalOpen(true);
    };


    if (isLoading) {
        return (
            <div className="text-center vh-100 my-5">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando producto...</span>
                </div>
                <p className="mt-2 text-muted">Cargando producto...</p>
            </div>
        );
    }

   
    if (!singleProduct) {
        return (
            <div className="d-flex align-items-center vh-100">
                <div className="text-center text-muted my-5 mx-auto">
                    No hay detalle disponible del producto.
                </div>
            </div>
        );
    }



    return (
        <>
            <main className="container-fluid py-4">
                <div className="d-flex header-h justify-content-between align-items-center mt-5 mb-3">
                    <h1 className="h5 mb-0 ms-4 product-title">{singleProduct.name}</h1>
                    <button onClick={() => navigate(-1)} className="btn back-btn me-4 rounded-lg d-flex align-items-center">
                        <i className="bi bi-arrow-left me-2"></i> Volver
                    </button>
                </div>
                <section className="detail-content-section p-4 rounded-lg">
                    <div className="row mb-4">
                        <div className="col-12">
                            <div className="row g-3">
                                <div className="col-md-6">
                                    <img
                                        src={imagePath(singleProduct, 0)}
                                        className="img-fluid gallery-main-image shadow-sm"
                                        alt={`Imagen principal de ${singleProduct.name}`}
                                        onError={handleImageError}
                                    />
                                </div>
                                <div className="col-md-6 d-none d-md-block">
                                    <div className="row g-3">
                                        {singleProduct.images.slice(1, 5).map((image, index) => (
                                            <div className="col-6" key={index}>
                                                <img
                                                    src={imagePath(singleProduct, index + 1)}
                                                    className="img-fluid gallery-thumbnail"
                                                    alt={`Miniatura ${index + 2}`}
                                                    onError={handleImageError}
                                                />
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                            <div className="d-flex justify-content-end mt-3">
                                <button className="btn btn-link text-primary text-decoration-none" onClick={showModal}>
                                    Ver más <i className="bi bi-arrow-right"></i>
                                </button>
                                {modalOpen && <ImageGalleryModal product={singleProduct} onClose={() => setModalOpen(false)} />}
                            </div>
                        </div>
                    </div>
                    <p className="product-detail-text lead mb-4" >{singleProduct.description}</p>
                    <div className="d-flex flex-column flex-md-row gap-1 align-items-center mt-auto">
                        <div className="w-100 text-start">
                            <span className="fs-4 fw-bold product-detail-text" >{singleProduct.price}</span>
                        </div>
                        <div className="w-100 text-center text-md-end">
                            <button className="btn back-btn btn-lg rounded-lg">
                                Reservar Ahora
                            </button>
                        </div>
                    </div>
                </section>
            </main>
        </>
    );
}

import { useState, useEffect } from "react";
import { ImageGalleryModal } from "../components/ImageGalleryModal";
import { useFetch } from "../hooks/useFetch";
import { useNavigate, useParams } from "react-router-dom";
import { useMessageModal } from "../context/MessageModalContext";
import { useCurrencyFormatter } from "../hooks/useCurrencyFormatter";
import "../styles/MainStyle.css";
import "../styles/ProductDetailStyle.css";
import { API_CONFIG } from "../config/apiConfig";

export const ProductDetailsComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para el mensaje
    const { formatCurrency } = useCurrencyFormatter();
    const { id } = useParams(); // <-- Obtener el ID de la URL
    const [singleProduct, setSingleProduct] = useState(null)
    const [modalOpen, setModalOpen] = useState(false)

    const URL = `${API_CONFIG.PRODUCTS}/${id}`; // <-- Usar el ID para construir la URL
    const { data, isLoading, error, fetchData } = useFetch();
    const [err, setErr] = useState(null);
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

    // Este useEffect se ejecuta cuando cambie el id.
    useEffect(() => {
        if (id) { // <-- Asegurarse de que el ID exista antes de llamar a la API
            fetchData(URL, 'GET');
        }
    }, [id, URL]);


    // Este useEffect procesa los datos una vez que han sido recibidos.
    useEffect(() => {
        if (data) {
            setSingleProduct(data);
        }

        if (error) {
            console.error(error);
            setErr("Error al cargar el producto: " + error.message);
            setModalMessage("Ocurrió un problema en la aplicación.");
        }
    }, [data, error, setModalMessage]);

    const imagePath = (item, index) => {
        return item?.productImages?.[index]?.imagePath
            ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${item.productImages[index].imagePath}`
            : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';
    };


    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    // Manejar errores del ícono
    const handleIconError = (e) => {
        e.target.onerror = null;
        e.target.src = 'https://placehold.co/48x48/E0F2FE/3B82F6?text=:(';
    };

    const showModal = () => {
        setModalOpen(true);
    };


    if (isLoading) {
        return (
            <div className="text-center vh-100 my-5">
                <div className="spinner-border text-primary" role="status">
                </div>
                <p className="mt-2 text-muted">Cargando producto...</p>
            </div>
        );
    }


    if (!singleProduct) {
        return (

            <div className="d-flex flex-column align-items-center vh-100">
                <div className="alert alert-danger text-center my-5 mx-auto">{err}</div>
                <div className="text-center text-muted my-5 mx-auto">
                    No hay detalle disponible del producto.
                </div>
            </div>

        );
    }



    return (
        <>
            <main className="min-vh-100 container-fluid py-4">
                <div className="container d-flex header-product-datail justify-content-between align-items-center mt-5 mb-3 sticky-top">
                    <h1 className="h5 mb-0 ms-4 product-title">{singleProduct.name}</h1>
                    <button onClick={() => navigate(-1)} className="btn back-btn me-4 rounded-3 d-flex align-items-center">
                        <i className="bi bi-arrow-left me-2"></i> Volver
                    </button>
                </div>
                <section className="container detail-content-section p-4 rounded-3">
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
                                        {singleProduct.productImages.slice(1, 5).map((image, index) => (
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
                                <button className="btn btn-link text-primary text-decoration-none fw-bold" onClick={showModal}>
                                    Ver más <i className="bi bi-arrow-right"></i>
                                </button>
                                {modalOpen && <ImageGalleryModal product={singleProduct} onClose={() => setModalOpen(false)} />}
                            </div>
                        </div>
                    </div>
                    <div>
                        <p className="product-detail-text lead mt-2 mb-3" >{singleProduct.description}</p>
                        <h3 className="fw-bold mt-5 product-feature-text">Características</h3>
                        <hr className="opacity-50"></hr>
                        <div className="d-flex flex-wrap">
                            {singleProduct.features.map((feature, index) => {
                                const IMAGE_URL = feature?.icon?.imagePath
                                    ? `${API_CONFIG.FEATURE_IMAGES_BASE}${feature.icon.imagePath}`
                                    : 'https://placehold.co/48x48/E0F2FE/3B82F6?text=?';
                                return (
                                    <div className="d-flex align-items-center mx-2 my-2" key={index} style={{ width: '165px' }}>
                                        <div className="me-1" style={{ width: '48px', height: '48px' }}>
                                            <img src={IMAGE_URL}
                                                alt={`Icon ${feature?.icon?.id}`}
                                                onError={handleIconError}
                                            />
                                        </div>
                                        <div className="product-detail-text">{feature.name}</div>
                                    </div>
                                );
                            })
                            }
                        </div>
                    </div>
                    <div className="d-flex flex-column flex-md-row gap-1 align-items-center mt-auto">
                        <div className="w-100 text-start">
                            <span className="fs-4 fw-bold product-detail-text" >{formatCurrency(singleProduct.price)}</span>
                            <small className="text-muted"> /día</small>
                        </div>
                        <div className="w-100 text-center text-md-end">
                            <button className="btn back-btn btn-lg rounded-3">
                                Reservar Ahora
                            </button>
                        </div>
                    </div>
                </section>
            </main>
        </>
    );
}

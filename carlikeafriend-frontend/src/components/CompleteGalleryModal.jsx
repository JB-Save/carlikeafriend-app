import { API_CONFIG } from "../config/apiConfig";

export const CompleteGalleryModal = ({ images, productName }) => {

    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    const imagePath = (items, index) => {
        return items[index]?.imagePath
            ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${items[index].imagePath}`
            : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';
    };

    return (
        <>
            {
                images.map((image, index) => {

                    return (
                        < img
                            key={index}
                            data-testid="gallery-image"
                            src={imagePath(images, index)}
                            className="img-fluid rounded-3 mb-3"
                            alt={`Imagen ${index + 1} de ${productName}`}
                            onError={handleImageError}
                            loading="lazy"
                        />
                    );

                })
            }

        </>

    );
}

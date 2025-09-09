
export const CompleteGalleryModal = ({ images, productName }) => {

    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    return (
        <>
            {
                images.map((image, index) => {
                    const imageUrl = images[index]?.imagePath || 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';
                    const url = `http://localhost:8080/carlikeafriend/products/images${imageUrl}`;

                    return (
                        < img
                            key={index}
                            data-testid="gallery-image"
                            src={url}
                            className="img-fluid rounded-lg mb-3"
                            alt={`Imagen ${index + 1} de ${productName}`}
                            onError={handleImageError}
                        />
                    );

                })
            }

        </>

    );
}

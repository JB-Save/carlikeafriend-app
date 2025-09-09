import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import { ImageGalleryModal } from '../components/ImageGalleryModal';

describe('ImageGalleryModal', () => {
    // Datos de producto de ejemplo para las pruebas.
    const mockProduct = {
        name: 'Coche Deportivo',
        images: [
            { id: 101, imagePath: '/path/to/image1.jpg' },
            { id: 102, imagePath: '/path/to/image2.jpg' }
        ],
    };

    const mockOnClose = vi.fn();

    // Resetear los mocks antes de cada prueba.
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('debe renderizar el modal con las imágenes del producto cuando se le pasa un producto', () => {
        // Renderizar el componente con el producto mock y el manejador de cierre.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        // Verificar que el título del modal contenga el nombre del producto.
        expect(screen.getByText(`Galería de Imágenes de ${mockProduct.name}`)).toBeInTheDocument();

        // Verificar que el componente hijo CompleteGallaryModal se renderice.
        // Contamos el número de imágenes renderizadas usando el data-testid.
        const images = screen.getAllByTestId('gallery-image');
        expect(images.length).toBe(mockProduct.images.length);

        // Verificar que la URL de la primera imagen sea correcta.
        expect(images[0]).toHaveAttribute('src', `http://localhost:8080/carlikeafriend/products/images${mockProduct.images[0].imagePath}`);
    });

    it('debe llamar a la función onClose cuando se hace clic en el botón de cerrar', () => {
        // Renderizar el componente con un producto y el mock de la función de cierre.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        // Obtener el botón de cerrar por su rol o texto.
         expect(screen.getByText('Cerrar')).toBeInTheDocument();
         screen.getByText('Cerrar').click();

        // Asegurarse de que la función onClose haya sido llamada.
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('debe cambiar el src de la imagen a un placeholder si la imagen original falla al cargar', async () => {
        // Renderizar el componente con el producto mock.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        // Obtener la primera imagen renderizada.
        const image = screen.getAllByTestId('gallery-image')[0];

        // Simular un evento de error de carga en la imagen.
        fireEvent.error(image);

        // Esperar a que el src de la imagen se actualice con el placeholder.
        await waitFor(() => {
            expect(image).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible');
        });
    });
});

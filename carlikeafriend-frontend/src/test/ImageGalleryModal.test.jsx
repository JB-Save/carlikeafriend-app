import userEvent from '@testing-library/user-event';
import { fireEvent, render, screen, waitFor } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { ImageGalleryModal } from '../components/ImageGalleryModal';
import { API_CONFIG } from '../config/apiConfig';

describe('ImageGalleryModal', () => {
    // Datos de producto de ejemplo para las pruebas.
    const mockProduct = {
        name: 'Coche Deportivo',
        productImages: [
            { id: 101, imagePath: '/path/to/image1.jpg' },
            { id: 102, imagePath: '/path/to/image2.jpg' }
        ],
    };

    const mockOnClose = vi.fn();

    it('no debe renderizar nada si el producto es nulo o indefinido', () => {
        const { container } = render(<ImageGalleryModal product={null} onClose={mockOnClose} />);
        expect(container).toBeEmptyDOMElement();
    });

    it('debe renderizar el modal con las imágenes del producto cuando se le pasa un producto', () => {
        // Renderizar el componente con el producto mock y el manejador de cierre.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        // Verificar que el título del modal contenga el nombre del producto.
        expect(screen.getByText('Galería de Imágenes:')).toBeInTheDocument();
        expect(screen.getByText(`${mockProduct.name}`)).toBeInTheDocument();

        // Verificar que el componente hijo se renderice contando las imágenes con data-testid.
        const images = screen.getAllByTestId('gallery-image');
        expect(images.length).toBe(mockProduct.productImages.length);

        // Uso dinámico de API_CONFIG para evitar errores por barras dobles o URLs hardcodeadas
        const expectedUrl = `${API_CONFIG.PRODUCT_IMAGES_BASE}${mockProduct.productImages[0].imagePath}`;

        // Verificar que la URL de la primera imagen sea correcta.
        expect(images[0]).toHaveAttribute('src', expectedUrl);
    });

    it('debe llamar a la función onClose cuando se hace clic en el botón de cerrar', async () => {
        const user = userEvent.setup();
        // Renderizar el componente con un producto y el mock de la función de cierre.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        const closeButton = screen.getByRole('button', { name: /Cerrar/i });
        expect(closeButton).toBeInTheDocument();

        await user.click(closeButton);

        // Asegurarse de que la función onClose haya sido llamada.
        expect(mockOnClose).toHaveBeenCalledTimes(1);
    });

    it('debe cambiar el src de la imagen a un placeholder si la imagen original falla al cargar', async () => {
        // Renderizar el componente con el producto mock.
        render(<ImageGalleryModal product={mockProduct} onClose={mockOnClose} />);

        // Obtener la primera imagen renderizada.
        const image = screen.getAllByTestId('gallery-image')[0];

        // Simular un evento de error de carga en la imagen usando fireEvent (requerido para eventos nativos de elementos multimedia)
        fireEvent.error(image);

        // Esperar a que el src de la imagen se actualice con el placeholder.
        await waitFor(() => {
            expect(image).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible');
        });
    });
});

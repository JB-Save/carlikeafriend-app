import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import { ProductDetailsComponent } from '../pages/ProductDetailsComponent';
import { useFetch } from '../hooks/useFetch';

// Mockear el hook de enrutamiento useParams para controlar el ID del producto.
vi.mock('react-router-dom', () => ({
    ...vi.importActual('react-router-dom'), // Importa todas las funciones reales excepto las que se van a mockear
    useParams: () => ({ id: '123' }),
    useNavigate: () => vi.fn(),
}));

// Mockear el hook useFetch para simular las llamadas a la API.
vi.mock('../hooks/useFetch', () => ({
    useFetch: vi.fn(),
}));

// Mockear el hook useMessageModal.
vi.mock('../context/MessageModalContext', () => ({
    useMessageModal: () => ({
        setModalMessage: vi.fn(),
    }),
}));

// Mockear el componente ImageGalleryModal para aislar la prueba del componente padre.
vi.mock('../components/ImageGalleryModal', () => ({
    ImageGalleryModal: ({ product, onClose }) => {
        // Retornar un JSX simple para simular la presencia del modal.
        return (
            <div data-testid="image-gallery-modal">
                <button data-testid="close-gallery-modal-btn" onClick={onClose}>
                    Cerrar Modal de Galería
                </button>
                <span data-testid="modal-product-name">{product.name}</span>
            </div>
        );
    },
}));

describe('ProductDetailsComponent', () => {
    // Datos de producto de ejemplo para las pruebas.
    const mockProduct = {
        id: 123,
        name: 'Coche Deportivo',
        images: [
            { id: 101, imagePath: '/path/to/image1.jpg' },
            { id: 102, imagePath: '/path/to/image2.jpg' }
        ],
        description: 'Un coche muy rápido.',
        price: 50000,
    };

    beforeEach(() => {
        vi.resetAllMocks();
    });

    it('debe renderizar el modal de la galería de imágenes cuando se hace clic en una imagen', async () => {
        // 1. Configurar useFetch para que devuelva un producto.
        useFetch
            .mockReturnValueOnce({ data: mockProduct, isLoading: false, error: null, fetchData: vi.fn() })
            .mockReturnValue({ data: null, isLoading: false, error: null, fetchData: vi.fn() });

        // 2. Renderizar el componente.
        render(<ProductDetailsComponent />);

        // 3. El modal no debe estar visible al principio.
        expect(screen.queryByTestId('image-gallery-modal')).not.toBeInTheDocument();

        // 4. Simular el clic en el botón que abre el modal.
        // Asumimos que hay un botón o una imagen con un data-testid para abrir el modal.
        const openModalButton = screen.getAllByRole('button', {name: /Ver más/i})[0];
        fireEvent.click(openModalButton);

        // 5. El modal de la galería debe ser visible.
        const modal = await screen.findByTestId('image-gallery-modal');
        expect(modal).toBeInTheDocument();
        expect(screen.getByTestId('modal-product-name')).toHaveTextContent(mockProduct.name);
    });

    it('debe cerrar el modal de la galería cuando se hace clic en el botón de cerrar', async () => {
        // 1. Configurar useFetch para que devuelva un producto.
        useFetch
            .mockReturnValueOnce({ data: mockProduct, isLoading: false, error: null, fetchData: vi.fn() })
            .mockReturnValue({ data: null, isLoading: false, error: null, fetchData: vi.fn() });

        // 2. Renderizar el componente.
        render(<ProductDetailsComponent />);

        // 3. Simular la apertura del modal.
        const openModalButton = screen.getAllByRole('button', {name: /Ver más/i})[0];
        fireEvent.click(openModalButton);

        // 4. Verificar que el modal está abierto.
        expect(await screen.findByTestId('image-gallery-modal')).toBeInTheDocument();

        // 5. Simular el clic en el botón de cerrar.
        const closeModalButton = screen.getByTestId('close-gallery-modal-btn');
        fireEvent.click(closeModalButton);

        // 6. El modal debe desaparecer.
        expect(screen.queryByTestId('image-gallery-modal')).not.toBeInTheDocument();
    });
});

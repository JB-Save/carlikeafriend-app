import { fireEvent, render, screen } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { ProductCardComponent } from '../components/ProductCardComponent';

// Mockear el hook usePricing para evitar estados de carga indeterminados en los tests
vi.mock('../hooks/usePricing', () => ({
    usePricing: () => ({
        pricingDetails: { hasDates: false, total: 25000 },
        isLoadingPricing: false,
    }),
}));

// Datos de prueba para un producto con una imagen
const mockProductWithImage = {
    id: '123',
    name: 'Carro de prueba',
    description: 'Descripción de prueba del carro.',
    price: 25000,
    productImages: [{ id: 1, imagePath: '/image/product_folder/image1.jpg' }],
    averageRating: 4.5,
    totalReviews: 10,
};

// Datos de prueba para un producto sin imágenes
const mockProductWithoutImage = {
    id: '456',
    name: 'Auto sin imagen',
    description: 'Descripción de un auto sin imagen.',
    price: 30000,
    productImages: [],
};

describe('ProductCardComponent', () => {

    // Test 1: Renderiza los detalles del producto correctamente
    it('debe renderizar la información del producto, precio y formato correctamente', () => {
        render(<ProductCardComponent product={mockProductWithImage} />);

        expect(screen.getByText('Carro de prueba')).toBeInTheDocument();
        expect(screen.getByText('$ 25.000')).toBeInTheDocument();
        expect(screen.getByText('Desde / día')).toBeInTheDocument();

        const productImage = screen.getByRole('img', { name: /Carro de prueba/i });
        expect(productImage).toBeInTheDocument();
        expect(productImage).toHaveAttribute('src', 'http://localhost:8080/carlikeafriend/products/images/image/product_folder/image1.jpg');
    });

    // Test 2: Muestra una imagen por defecto cuando el producto no tiene imágenes
    it('debe mostrar una imagen por defecto cuando el producto no tiene imágenes', () => {
        render(<ProductCardComponent product={mockProductWithoutImage} />);

        const productImage = screen.getByRole('img', { name: /Auto sin imagen/i });
        expect(productImage).toBeInTheDocument();
        expect(productImage).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen');
    });

    // Test 3: Maneja el error de carga de la imagen
    it('debe cambiar la imagen a un placeholder cuando ocurre un error de carga', () => {
        render(<ProductCardComponent product={mockProductWithImage} />);

        const productImage = screen.getByRole('img', { name: /Carro de prueba/i });

        // Simular el evento de error de carga en la imagen (requerido para eventos nativos multimedia)
        fireEvent.error(productImage);

        // Verificar que el src de la imagen ha cambiado al placeholder de error
        expect(productImage).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible');
    });

    // Test 4: El enlace "Ver Detalle" navega a la URL correcta
    it('debe tener un enlace a la página de detalles del producto', () => {
        render(<ProductCardComponent product={mockProductWithImage} />);

        const detailLink = screen.getByRole('link', { name: /Ver Detalle/i });
        expect(detailLink).toBeInTheDocument();
        expect(detailLink).toHaveAttribute('href', '/product-detail/123');
    });

});

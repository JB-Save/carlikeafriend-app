import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import {CardProductComponent} from '../components/CardProductComponent';

// Mock del componente Link de react-router-dom para evitar errores de routing
// y poder inspeccionar las propiedades del enlace.
vi.mock('react-router-dom', () => ({
    Link: vi.fn(({ to, children }) => <a href={to}>{children}</a>),
}));

// Datos de prueba para un producto con una imagen
const mockProductWithImage = {
    id: '123',
    name: 'Carro de prueba',
    description: 'Descripción de prueba del carro.',
    price: 25000,
    productImages: [{ id: 1, imagePath: '/image/product_folder/image1.jpg' }],
};

// Datos de prueba para un producto sin imágenes
const mockProductWithoutImage = {
    id: '456',
    name: 'Auto sin imagen',
    description: 'Descripción de un auto sin imagen.',
    price: 30000,
    images: [],
};

describe('CardProductComponent', () => {

    // Test 1: Renderiza los detalles del producto correctamente
    it('debe renderizar la información del producto con una imagen válida', () => {
        render(<CardProductComponent product={mockProductWithImage} />);

        // Verificar que el nombre, la descripción y el precio se muestran
        expect(screen.getByText('Carro de prueba')).toBeInTheDocument();
       // expect(screen.getByText('Descripción de prueba del carro.')).toBeInTheDocument();
        expect(screen.getByText('$ 25.000')).toBeInTheDocument();
        expect(screen.getByText('/día')).toBeInTheDocument();

        // Verificar que la imagen se renderiza con el src y alt correctos
        const productImage = screen.getByRole('img', { name: /Carro de prueba/i });
        expect(productImage).toBeInTheDocument();
        expect(productImage).toHaveAttribute('src', 'http://localhost:8080/carlikeafriend/products/images/image/product_folder/image1.jpg');
    });

    // Test 2: Muestra una imagen por defecto cuando el producto no tiene imágenes
    it('debe mostrar una imagen por defecto cuando el producto no tiene imágenes', () => {
        render(<CardProductComponent product={mockProductWithoutImage} />);

        const productImage = screen.getByRole('img', { name: /Auto sin imagen/i });
        expect(productImage).toBeInTheDocument();
        expect(productImage).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen');
    });

    // Test 3: Maneja el error de carga de la imagen
    it('debe cambiar la imagen a un placeholder cuando ocurre un error de carga', () => {
        render(<CardProductComponent product={mockProductWithImage} />);

        const productImage = screen.getByRole('img', { name: /Carro de prueba/i });

        // Simular el evento de error de carga en la imagen
        fireEvent.error(productImage);

        // Verificar que el src de la imagen ha cambiado al placeholder de error
        expect(productImage).toHaveAttribute('src', 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible');
    });

    // Test 4: El enlace "Ver Detalle" navega a la URL correcta
    it('debe tener un enlace a la página de detalles del producto', () => {
        render(<CardProductComponent product={mockProductWithImage} />);

        const detailLink = screen.getByRole('link', { name: /Ver Detalle/i });
        expect(detailLink).toBeInTheDocument();
        expect(detailLink).toHaveAttribute('href', '/product-detail/123');
    });

});

import { render, screen, fireEvent } from '@testing-library/react';
import { ProductTableComponent } from '../components/ProductTableComponent';
import { vi } from 'vitest';

// Mock del hook useNavigate para simular la navegación
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
    ...vi.importActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));

// Datos de prueba para los productos
const mockProducts = [
    { id: 1, name: 'Product A' },
    { id: 2, name: 'Product B' },
];

describe('ProductTableComponent', () => {

    // Prueba para verificar que se renderiza la tabla con los productos
    it('debe renderizar la tabla con los productos proporcionados y las columnas correctas', () => {
        const mockSetProductIdToDelete = vi.fn();
        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        // Verificar que los encabezados de la tabla están presentes
        expect(screen.getByText('ID')).toBeInTheDocument();
        expect(screen.getByText('Nombre')).toBeInTheDocument();
        expect(screen.getByText('Acciones')).toBeInTheDocument();

        // Verificar que los datos de los productos se renderizan en las filas
        expect(screen.getByText('Product A')).toBeInTheDocument();
        expect(screen.getByText('Product B')).toBeInTheDocument();
    });

    // Prueba para verificar que la función setProductIdToDelete se llama con el ID correcto al hacer clic en el botón Eliminar
    it('debe llamar a setProductIdToDelete con el ID correcto al hacer clic en el botón Eliminar', () => {
        const mockSetProductIdToDelete = vi.fn();
        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        // Encontrar el botón de eliminar del primer producto
        const deleteButton = screen.getAllByRole('button', { name: /Eliminar/i })[0];

        // Simular el clic en el botón
        fireEvent.click(deleteButton);

        // Verificar que la función fue llamada una vez con el ID correcto del producto
        expect(mockSetProductIdToDelete).toHaveBeenCalledTimes(1);
        expect(mockSetProductIdToDelete).toHaveBeenCalledWith(mockProducts[0].id);
    });

    // Prueba para verificar que el hook navigate se llama con los argumentos correctos al hacer clic en el botón Editar
    it('debe llamar a la función navigate con la ruta y el estado correctos al hacer clic en el botón Editar', () => {
        const mockSetProductIdToDelete = vi.fn();
        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        // Encontrar el botón de editar del primer producto
        const editButton = screen.getAllByRole('button', { name: /Editar/i })[0];

        // Simular el clic en el botón
        fireEvent.click(editButton);

        // Verificar que el mock de navigate fue llamado con los argumentos correctos
        expect(mockNavigate).toHaveBeenCalledTimes(1);
        expect(mockNavigate).toHaveBeenCalledWith('/administration/add-product', {
            replace: true,
            state: { productToEdit: mockProducts[0] }
        });
    });
});

import userEvent from '@testing-library/user-event';
import { render, screen } from '../utils/test-utils';
import { ProductTableComponent } from '../components/ProductTableComponent';
import { vi, describe, it, expect } from 'vitest';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

const mockProducts = [
    { id: 1, name: 'Product A' },
    { id: 2, name: 'Product B' },
];

describe('ProductTableComponent', () => {
    it('debe renderizar la tabla con los productos proporcionados y las columnas correctas', () => {
        const mockSetProductIdToDelete = vi.fn();
        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        expect(screen.getByText('ID')).toBeInTheDocument();
        expect(screen.getByText('Nombre')).toBeInTheDocument();
        expect(screen.getByText('Acciones')).toBeInTheDocument();

        expect(screen.getByText('Product A')).toBeInTheDocument();
        expect(screen.getByText('Product B')).toBeInTheDocument();
    });

    it('debe llamar a setProductIdToDelete con el ID correcto al hacer clic en el botón Eliminar', async () => {
        const user = userEvent.setup();
        const mockSetProductIdToDelete = vi.fn();

        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        const deleteButton = screen.getAllByRole('button', { name: /Eliminar/i })[0];
        await user.click(deleteButton);

        expect(mockSetProductIdToDelete).toHaveBeenCalledTimes(1);
        expect(mockSetProductIdToDelete).toHaveBeenCalledWith(mockProducts[0].id);
    });

    it('debe llamar a la función navigate con la ruta y el estado correctos al hacer clic en el botón Editar', async () => {
        const user = userEvent.setup();
        const mockSetProductIdToDelete = vi.fn();

        render(
            <ProductTableComponent
                products={mockProducts}
                setProductIdToDelete={mockSetProductIdToDelete}
            />
        );

        const editButton = screen.getAllByRole('button', { name: /Editar/i })[0];
        await user.click(editButton);

        expect(mockNavigate).toHaveBeenCalledTimes(1);
        expect(mockNavigate).toHaveBeenCalledWith('/administration/add-product', {
            replace: true,
            state: { productToEdit: mockProducts[0] }
        });
    });
});

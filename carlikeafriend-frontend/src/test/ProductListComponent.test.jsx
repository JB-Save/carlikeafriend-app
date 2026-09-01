import userEvent from '@testing-library/user-event';
import { describe, expect, vi, it } from 'vitest';
import { render, screen, waitFor } from '../utils/test-utils';
import { UserContext } from '../context/UserContext';
import { server } from '../setupTests';
import { http, HttpResponse } from 'msw';

import { ProductListComponent } from '../components/ProductListComponent';

// 1. Mocks de utilidades 
vi.mock('../utils/extractErrorMessage', () => ({
    extractErrorMessage: vi.fn(() => Promise.resolve("Error del servidor")),
}));

vi.mock('../utils/handleUnauthorizedError', () => ({
    handleUnauthorizedError: vi.fn(() => false),
}));

// 2. Mocks de componentes UI
vi.mock('../components/ProductTableComponent', () => ({
    ProductTableComponent: vi.fn(({ products, setProductIdToDelete }) => (
        <div data-testid="product-table">
            {products?.map((product) => (
                <button key={product.id} onClick={() => setProductIdToDelete(product.id)}>
                    Eliminar {product.name}
                </button>
            ))}
        </div>
    )),
}));

vi.mock('../components/DeleteConfirmationModalComponent', () => ({
    DeleteConfirmationModalComponent: vi.fn(({ id, deleteFunction, onClose, isDeleting }) => (
        <div data-testid="delete-modal">
            <button onClick={() => deleteFunction(id)} disabled={isDeleting}>
                {isDeleting ? 'Eliminando...' : 'Eliminar'}
            </button>
            <button onClick={onClose}>Cancelar</button>
        </div>
    )),
}));

describe('ProductListComponent', () => {

    // Helper: Usa el customRender de test-utils pero inyecta el token explícitamente
    const renderWithToken = () => {
        return render(
            <UserContext.Provider value={{ token: 'token-valido-123', logout: vi.fn() }}>
                <ProductListComponent />
            </UserContext.Provider>
        );
    };

    it('debe mostrar el estado de carga y luego la tabla con datos de MSW', async () => {
        renderWithToken();

        expect(screen.getByText(/Cargando productos.../i)).toBeInTheDocument();

        // MSW interceptará '*/carlikeafriend/products' gracias a handlers.js
        await waitFor(() => {
            expect(screen.getByTestId('product-table')).toBeInTheDocument();
        });

        // Verificamos que llegó "Producto Prueba" definido en tus handlers
        expect(screen.getByText('Eliminar Producto Prueba')).toBeInTheDocument();
    });

    it('debe manejar el flujo completo de eliminación exitosa', async () => {
        const user = userEvent.setup();
        renderWithToken();

        const deleteTrigger = await screen.findByText('Eliminar Producto Prueba');
        await user.click(deleteTrigger);

        const confirmBtn = screen.getByRole('button', { name: 'Eliminar' });
        await user.click(confirmBtn);

        // Al ejecutarse el DELETE, MSW devolverá el status 204 definido en handlers.js
        await waitFor(() => {
            expect(screen.getByText(/Producto eliminado exitosamente/i)).toBeInTheDocument();
        });
    });

    it('debe mostrar error si la eliminación falla (sobrescribiendo MSW)', async () => {
        const user = userEvent.setup();

        // Modificamos el servidor SOLO para este test para que el DELETE falle
        server.use(
            http.delete('*/carlikeafriend/products/:id', () => {
                return new HttpResponse(null, { status: 400 });
            })
        );

        renderWithToken();

        const deleteTrigger = await screen.findByText('Eliminar Producto Prueba');
        await user.click(deleteTrigger);

        const confirmBtn = screen.getByRole('button', { name: 'Eliminar' });
        await user.click(confirmBtn);

        await waitFor(() => {
            expect(screen.getByText('Error del servidor')).toBeInTheDocument();
        });
    });
});
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, vi, it } from 'vitest';
import { ProductListComponent } from '../components/ProductListComponent';
import { UserContext } from '../context/UserContext';
import { MessageModalContext } from '../context/MessageModalContext';
import { BrowserRouter } from 'react-router-dom';

// 1. Mocks de sub-componentes alineados con el código real
vi.mock('../components/ProductTableComponent', () => ({
    ProductTableComponent: vi.fn(({ products, setProductIdToDelete }) => (
        <div data-testid="product-table">
            {products.map((product) => (
                <button key={product.id} onClick={() => setProductIdToDelete(product.id)}>
                    Eliminar {product.name}
                </button>
            ))}
        </div>
    )),
}));

// Mock del modal usando las props reales del archivo que enviaste
vi.mock('../components/DeleteConfirmationModalComponent', () => ({
    DeleteConfirmationModalComponent: vi.fn(({ id, deleteFunction, onClose, objectName, isDeleting }) => (
        <div data-testid="delete-modal">
            <p>¿Estás seguro de que quieres eliminar {objectName} con ID: {id}?</p>
            <button onClick={() => deleteFunction(id)} disabled={isDeleting}>
                {isDeleting ? 'Eliminando...' : 'Eliminar'}
            </button>
            <button onClick={onClose}>Cancelar</button>
        </div>
    )),
}));

// Mocks de utilerías
vi.mock('../utils/extractErrorMessage', () => ({
    extractErrorMessage: vi.fn(() => Promise.resolve("Error del servidor")),
}));

vi.mock('../utils/handleUnauthorizedError', () => ({
    handleUnauthorizedError: vi.fn(() => false),
}));

describe('ProductListComponent', () => {
    const mockToken = 'fake-token';
    const mockLogout = vi.fn();
    const mockSetModalMessage = vi.fn();
    const mockProducts = [{ id: '101', name: 'Producto Prueba' }];

    beforeEach(() => {
        vi.clearAllMocks();
        global.fetch = vi.fn();
    });

    const renderComponent = () => {
        return render(
            <BrowserRouter>
                <UserContext.Provider value={{ token: mockToken, logout: mockLogout }}>
                    <MessageModalContext.Provider value={{ setModalMessage: mockSetModalMessage }}>
                        <ProductListComponent />
                    </MessageModalContext.Provider>
                </UserContext.Provider>
            </BrowserRouter>
        );
    };

    it('debe mostrar el estado de carga y luego la tabla', async () => {
        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockProducts,
        });

        renderComponent();
        
        expect(screen.getByText(/Cargando productos.../i)).toBeInTheDocument();

        await waitFor(() => {
            expect(screen.getByTestId('product-table')).toBeInTheDocument();
        });
    });

    it('debe manejar el flujo completo de eliminación', async () => {
        // 1. Mock de carga inicial y luego de eliminación exitosa
        global.fetch
            .mockResolvedValueOnce({ ok: true, json: async () => mockProducts }) // Carga
            .mockResolvedValueOnce({ ok: true }); // Delete (204/200)

        renderComponent();

        // 2. Abrir el modal
        const deleteTrigger = await screen.findByText('Eliminar Producto Prueba');
        fireEvent.click(deleteTrigger);

        // 3. Verificar que el modal recibió las props correctas (objectName e id)
        expect(screen.getByText(/eliminar este producto con ID: 101/i)).toBeInTheDocument();

        // 4. Confirmar eliminación
        const confirmBtn = screen.getByRole('button', { name: 'Eliminar' });
        fireEvent.click(confirmBtn);

        // 5. Verificar mensaje de éxito
        await waitFor(() => {
            expect(screen.getByText(/Producto eliminado exitosamente/i)).toBeInTheDocument();
        });
    });

    it('debe mostrar error si la eliminación falla', async () => {
        global.fetch
            .mockResolvedValueOnce({ ok: true, json: async () => mockProducts })
            .mockResolvedValueOnce({ ok: false, status: 400 }); // Fallo en delete

        renderComponent();

        const deleteTrigger = await screen.findByText('Eliminar Producto Prueba');
        fireEvent.click(deleteTrigger);

        const confirmBtn = screen.getByRole('button', { name: 'Eliminar' });
        fireEvent.click(confirmBtn);

        await waitFor(() => {
            expect(screen.getByText('Error del servidor')).toBeInTheDocument();
            expect(screen.getByText('Error del servidor')).toHaveClass('alert-danger');
        });
    });
});
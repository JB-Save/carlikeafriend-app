import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, vi } from 'vitest';
import { ProductListComponent } from '../components/ProductListComponent';
import { useFetch } from '../hooks/useFetch';
import { useMessageModal } from '../context/MessageModalContext';

// Mocks de los componentes y hooks para aislar ProductListComponent
vi.mock('../components/ProductTableComponent', () => ({
    ProductTableComponent: vi.fn(({ products, handleDeleteClick }) => (
        <div data-testid="product-table">
            {/* Simula una fila de la tabla con un botón de eliminar */}
            {products.map((product) => (
                <button key={product.id} onClick={() => handleDeleteClick(product.id)}>
                    Eliminar {product.id}
                </button>
            ))}
        </div>
    )),
}));


vi.mock('../components/DeleteConfirmationModalComponent', () => ({
    DeleteConfirmationModalComponent: vi.fn(({ isModalOpen, onConfirm, onCancel }) =>
        isModalOpen ? (
            <div data-testid="delete-modal">
                <button onClick={onConfirm}>Confirmar Eliminación</button>
                <button onClick={onCancel}>Cancelar</button>
            </div>
        ) : null
    ),
}));

// Mock del hook useFetch para controlar los datos de la API
vi.mock('../hooks/useFetch', () => ({
    useFetch: vi.fn(() => ({
        data: null,
        isLoading: false,
        error: null,
        fetchData: vi.fn(),
    })),
}));

// Mock del hook useMessageModal para el contexto del modal
vi.mock('../context/MessageModalContext', () => ({
    useMessageModal: vi.fn(() => ({
        setModalMessage: vi.fn(),
    })),
}));

describe('ProductListComponent', () => {
    // Datos de prueba
    const mockProducts = [
        { id: '1', name: 'Carro A', },
        { id: '2', name: 'Carro B', },
    ];

    // Limpia los mocks antes de cada prueba para evitar interferencias
    beforeEach(() => {
        vi.clearAllMocks();
    });

    //Muestra el spinner de carga al cargar los datos
    it('debe mostrar el spinner de carga mientras se cargan los productos', () => {
        useFetch
            .mockReturnValueOnce({ data: null, isLoading: true, error: null, fetchData: vi.fn() })
            .mockReturnValue({ data: null, isLoading: false, error: null, fetchData: vi.fn() });

        render(<ProductListComponent />);
        expect(screen.getByText('Cargando productos...')).toBeInTheDocument();
    });

    // Muestra el mensaje de error si la carga falla
    it('debe mostrar un mensaje de error si falla la carga de productos', async () => {
        useFetch
            .mockReturnValueOnce({ data: null, isLoading: false, error: 'Error al cargar la lista de productos. Por favor, inténtalo de nuevo.', fetchData: vi.fn() })
            .mockReturnValue({ data: null, isLoading: false, error: null, fetchData: vi.fn() });

        render(<ProductListComponent />);
        await waitFor(() => {
            expect(screen.getByText('Error al cargar la lista de productos. Por favor, inténtalo de nuevo.')).toBeInTheDocument();
        });
    });

    // Renderiza la tabla de productos con datos
    it('debe renderizar la tabla de productos cuando se cargan los datos correctamente', async () => {
        useFetch
            .mockReturnValueOnce({ data: mockProducts, isLoading: false, error: null, fetchData: vi.fn() })
            .mockReturnValue({ data: null, isLoading: false, error: null, fetchData: vi.fn() });

        render(<ProductListComponent />);
        await waitFor(() => {
            expect(screen.getByTestId('product-table')).toBeInTheDocument();
        });
    });
})

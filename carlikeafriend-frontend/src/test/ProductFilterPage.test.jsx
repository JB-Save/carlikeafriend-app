import { render, screen, fireEvent } from '@testing-library/react';
import { beforeEach, describe, expect, vi, it } from 'vitest';
import { ProductFilterPage } from '../pages/ProductFilterPage';
import { useProductFilter } from '../hooks/useProductFilter';
import { BrowserRouter } from 'react-router-dom';

// 1. Mock de hooks y componentes externos
vi.mock('../hooks/useProductFilter', () => ({
    useProductFilter: vi.fn(),
}));

vi.mock('../components/CardProductComponent', () => ({
    CardProductComponent: vi.fn(({ product }) => (
        <div data-testid="product-card">{product.name}</div>
    )),
}));

// Mock del formateador de moneda para evitar errores de Intl
vi.mock('../hooks/useCurrencyFormatter', () => ({
    useCurrencyFormatter: () => ({
        formatCurrency: (val) => `$ ${val}`,
    }),
}));

describe('ProductFilterPage - Pruebas Integrales', () => {
    const mockHandlers = {
        handleCheckListChange: vi.fn(),
        handlePriceChange: vi.fn(),
        handleSortChange: vi.fn(),
        resetFilters: vi.fn(),
    };

    const mockInitialState = {
        allCategories: [{ id: 1, name: 'SUV' }],
        allFeatures: [{ id: 1, name: 'Aire Acondicionado' }],
        isLoadingCategory: false,
        isLoadingFeature: false,
        products: [{ id: '1', name: 'Carro Test', price: 1000 }],
        isLoadingProduct: false,
        productError: null,
        absoluteMinPrice: 0,
        absoluteMaxPrice: 100000,
        filteredProducts: { categories: [], features: [], minPrice: 0, maxPrice: 100000, sortBy: 'price_asc' },
        selectDefaultOption: 'price_asc',
        ...mockHandlers
    };

    beforeEach(() => {
        vi.clearAllMocks();
        useProductFilter.mockReturnValue(mockInitialState);
    });

    const renderPage = () => render(
        <BrowserRouter>
            <ProductFilterPage />
        </BrowserRouter>
    );

    it('debe renderizar correctamente y mostrar productos', () => {
        renderPage();
        expect(screen.getByTestId('product-card')).toBeInTheDocument();
    });

    it('debe llamar a handleCheckListChange al seleccionar una categoría', () => {
        renderPage();
        const checkbox = screen.getByLabelText('SUV');

        fireEvent.click(checkbox);

        expect(mockHandlers.handleCheckListChange).toHaveBeenCalledWith(
            1,
            true,
            "categories"
        );
    });

    it('debe llamar a handlePriceChange con valores numéricos', () => {
        renderPage();

        // Usamos getAllByDisplayValue y tomamos el primer elemento [0]
        const minPriceInputs = screen.getAllByDisplayValue('0');
        const maxPriceInputs = screen.getAllByDisplayValue('100000');

        // Simulamos el cambio
        fireEvent.change(minPriceInputs[0], { target: { value: '5000' } });

        // El test espera un NÚMERO, ya que el hook lo convierte internamente
        expect(mockHandlers.handlePriceChange).toHaveBeenCalledWith('minPrice', 5000);

        fireEvent.change(maxPriceInputs[0], { target: { value: '80000' } });
        expect(mockHandlers.handlePriceChange).toHaveBeenCalledWith('maxPrice', 80000);
    });

    it('debe llamar a handleSortChange al cambiar el ordenamiento', () => {
        renderPage();
        // El select tiene el label "Ordenar por"
        const sortSelect = screen.getByLabelText(/Ordenar por/i);
        fireEvent.change(sortSelect, { target: { value: 'price_desc' } });
        expect(mockHandlers.handleSortChange).toHaveBeenCalledWith('price_desc');
    });

    it('debe ejecutar resetFilters al hacer clic en el botón de limpiar (evitando duplicados)', () => {
        renderPage();

        // Buscamos todos los botones con ese texto y usamos el primero, 
        // o filtramos por una clase específica si existe.
        const resetButtons = screen.getAllByRole('button', { name: /Limpiar Filtros/i });

        // Hacemos clic en el primero (normalmente el de la vista desktop)
        fireEvent.click(resetButtons[0]);

        expect(mockHandlers.resetFilters).toHaveBeenCalled();
    });

    it('debe mostrar mensaje de carga cuando isLoadingProduct es true', () => {
        useProductFilter.mockReturnValue({
            ...mockInitialState,
            isLoadingProduct: true,
            products: []
        });
        renderPage();
        expect(screen.getByText(/Cargando productos.../i)).toBeInTheDocument();
    });

    it('debe mostrar alerta cuando no hay resultados', () => {
        useProductFilter.mockReturnValue({
            ...mockInitialState,
            products: [],
            isLoadingProduct: false
        });
        renderPage();
        expect(screen.getByText(/No se encontraron productos con los filtros seleccionados/i)).toBeInTheDocument();
    });
});
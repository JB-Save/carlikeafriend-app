import userEvent from '@testing-library/user-event';
import { fireEvent, render, screen } from '../utils/test-utils';
import { beforeEach, describe, expect, vi, it } from 'vitest';
import { ProductFilterPage } from '../pages/ProductFilterPage';
import { useProductFilter } from '../hooks/useProductFilter';

// 1. Mocks de hooks y componentes externos
vi.mock('../hooks/useProductFilter', () => ({
    useProductFilter: vi.fn(),
}));

vi.mock('../components/ProductCardComponent', () => ({
    ProductCardComponent: ({ product }) => (
        <div data-testid="product-card">{product.name}</div>
    ),
}));

vi.mock('../components/BookingSearchForm', () => ({
    BookingSearchForm: () => <div data-testid="booking-search-form">Search Form Mock</div>,
}));

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
        products: [{ id: '1', name: 'Carro Test', price: 1000, categories: [{ id: 1 }] }],
        isLoadingProduct: false,
        productError: null,
        absoluteMinPrice: 0,
        absoluteMaxPrice: 100000,
        filteredProducts: { categories: [], features: [], minPrice: 0, maxPrice: 100000, sortBy: 'price_asc' },
        selectDefaultOption: 'price_asc',
        ...mockHandlers
    };

    beforeEach(() => {
        useProductFilter.mockReturnValue(mockInitialState);
    });

    it('debe renderizar correctamente y mostrar productos', () => {
        render(<ProductFilterPage />);
        expect(screen.getByTestId('product-card')).toBeInTheDocument();
    });

    it('debe llamar a handleCheckListChange al seleccionar una categoría', async () => {
        const user = userEvent.setup();

        render(<ProductFilterPage />);

        const checkbox = screen.getByRole('checkbox', { name: /SUV/i });

        // Ahora el checkbox ya no debería estar deshabilitado
        expect(checkbox).not.toBeDisabled();

        await user.click(checkbox);

        expect(mockHandlers.handleCheckListChange).toHaveBeenCalledWith(
            1,
            true,
            "categories"
        );
    });

    it('debe llamar a handlePriceChange con valores numéricos', async () => {
        render(<ProductFilterPage />);

        // 2. Cambiamos spinbutton por slider
        const sliders = screen.getAllByRole('slider');
        const minSlider = sliders[0]; // Mínimo
        const maxSlider = sliders[1]; // Máximo

        // Para inputs de tipo range, usamos fireEvent.change ya que 
        // userEvent no es ideal para sliders de rango
        fireEvent.change(minSlider, { target: { value: '5000' } });
        expect(mockHandlers.handlePriceChange).toHaveBeenCalledWith('minPrice', 5000);

        fireEvent.change(maxSlider, { target: { value: '80000' } });
        expect(mockHandlers.handlePriceChange).toHaveBeenCalledWith('maxPrice', 80000);
    });

    it('debe llamar a handleSortChange al cambiar el ordenamiento', async () => {
        const user = userEvent.setup();
        render(<ProductFilterPage />);

        const sortSelect = screen.getByLabelText(/Ordenar por/i);
        await user.selectOptions(sortSelect, 'price_desc');

        expect(mockHandlers.handleSortChange).toHaveBeenCalledWith('price_desc');
    });

    it('debe ejecutar resetFilters al hacer clic en el botón de limpiar', async () => {
        const user = userEvent.setup();
        render(<ProductFilterPage />);

        const resetButtons = screen.getAllByRole('button', { name: /Limpiar Filtros/i });
        await user.click(resetButtons[0]);

        expect(mockHandlers.resetFilters).toHaveBeenCalled();
    });

    it('debe mostrar mensaje de carga cuando isLoadingProduct es true', () => {
        useProductFilter.mockReturnValue({
            ...mockInitialState,
            isLoadingProduct: true,
            products: []
        });

        render(<ProductFilterPage />);
        expect(screen.getByText(/Buscando los mejores vehículos.../i)).toBeInTheDocument();
    });

    it('debe mostrar alerta cuando no hay resultados', () => {
        useProductFilter.mockReturnValue({
            ...mockInitialState,
            products: [],
            isLoadingProduct: false
        });

        render(<ProductFilterPage />);
        expect(screen.getByText(/No se encontraron productos con los filtros seleccionados/i)).toBeInTheDocument();
    });
});
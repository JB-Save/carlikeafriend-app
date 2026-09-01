import userEvent from '@testing-library/user-event';
import { render, screen } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { ProductDetailsComponent } from '../pages/ProductDetailsComponent';

// 1. Mock de react-router-dom SOLO para useParams. 
// El BrowserRouter ya viene en test-utils.jsx
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useParams: () => ({ id: '1' }),
    };
});

// 2. Mocks de Hooks de UI y Lógica Compleja (Aislamos estas responsabilidades)
vi.mock('../hooks/useCurrencyFormatter', () => ({
    useCurrencyFormatter: () => ({ formatCurrency: (val) => `$${val}` }),
}));

vi.mock('../hooks/usePricing', () => ({
    usePricing: () => ({ pricingDetails: { hasDates: false }, isLoadingPricing: false }),
}));

vi.mock('../hooks/useSocialShare', () => ({
    useSocialShare: () => ({ logShareInteraction: vi.fn() }),
}));

// 3. Mocks de Subcomponentes (Evitamos renderizar árboles gigantes)
vi.mock('../components/BookingSearchForm', () => ({
    BookingSearchForm: () => <div data-testid="booking-search-form">Form</div>
}));
vi.mock('../components/ProductReviewsComponent', () => ({
    ProductReviewsComponent: () => <div data-testid="reviews-component">Reviews</div>
}));
vi.mock('../components/ImageGalleryModal', () => ({
    ImageGalleryModal: ({ onClose }) => (
        <div data-testid="gallery-modal">
            <button onClick={onClose}>Cerrar Modal</button>
        </div>
    )
}));

describe('ProductDetailsComponent', () => {

    it('debe renderizar el producto desde MSW y abrir la galería al hacer clic en "Ver más"', async () => {
        const user = userEvent.setup();

        // Utilizamos el customRender.
        render(<ProductDetailsComponent />);

        // 1. Verificamos estado de carga inicial
        expect(screen.getByText(/Cargando producto y sucursales/i)).toBeInTheDocument();

        // 2. Esperamos a que MSW devuelva el producto 'Auto Test' (definido en handlers.js)
        const title = await screen.findByText('Auto Test');
        expect(title).toBeInTheDocument();

        // 3. Probamos la interacción del usuario
        const verMasButton = screen.getByRole('button', { name: /Ver más/i });
        await user.click(verMasButton);

        // 4. Confirmamos la apertura del modal mockeado
        const modal = await screen.findByTestId('gallery-modal');
        expect(modal).toBeInTheDocument();
    });
});
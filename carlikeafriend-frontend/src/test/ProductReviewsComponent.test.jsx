import { render, screen, waitFor } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { ProductReviewsComponent } from '../components/ProductReviewsComponent';

// 1. Mock de react-router-dom
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useNavigate: () => vi.fn()
    };
});

// 2. Mock de utilidades de texto
vi.mock('../utils/stringHelpers', () => ({
    adaptStringToUserObject: vi.fn(),
    getFormattedName: () => 'Usuario Test',
    getInitials: () => 'UT',
    getAvatarColor: () => '#000'
}));

describe('ProductReviewsComponent', () => {

    it('debe renderizar las reseñas luego de hacer fetch', async () => {
        // 3. Usamos el customRender.
        render(<ProductReviewsComponent productId="1" />);

        // 4. Esperamos a que los datos mockeados de MSW se rendericen en pantalla
        await waitFor(() => {
            expect(screen.getByText('"Excelente vehículo"')).toBeInTheDocument();
            expect(screen.getByText('Usuario Test')).toBeInTheDocument();
        });
    });
});
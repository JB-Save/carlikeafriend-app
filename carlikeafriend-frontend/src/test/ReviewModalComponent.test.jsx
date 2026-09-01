import userEvent from '@testing-library/user-event';
import { render, screen } from '../utils/test-utils';
import { ReviewModalComponent } from '../components/ReviewModalComponent';
import { vi, describe, it, expect } from 'vitest';

// 1. Mocks limpios usando importOriginal para preservar la integridad de los contextos globales
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

vi.mock('../context/MessageModalContext', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useMessageModal: () => ({
            showModal: false,
            modalMessage: '',
            setModalMessage: vi.fn(), // Aseguramos que la función exista para el componente
        }),
    };
});

vi.mock('../config/apiConfig', () => ({
    API_CONFIG: { PRODUCT_REVIEWS: '/api/reviews' }
}));

describe('ReviewModalComponent', () => {

    it('debe mostrar mensaje de error si se envía sin seleccionar estrellas', async () => {
        const user = userEvent.setup();
        const mockOnClose = vi.fn();

        render(<ReviewModalComponent productId="1" onClose={mockOnClose} onSuccess={vi.fn()} />);

        const submitBtn = screen.getByRole('button', { name: /Publicar Reseña/i });
        await user.click(submitBtn);

        expect(screen.getByText('Por favor, selecciona una puntuación de 1 a 5 estrellas.')).toBeInTheDocument();
    });

    it('debe renderizar el título correctamente', () => {
        render(<ReviewModalComponent productId="1" onClose={vi.fn()} onSuccess={vi.fn()} />);

        expect(screen.getByText('Calificar Vehículo')).toBeInTheDocument();
    });
});
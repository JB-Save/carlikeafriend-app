import userEvent from '@testing-library/user-event';
import { render, screen } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { ShareModalComponent } from '../components/ShareModalComponent';

const mockShareToNetwork = vi.fn();
vi.mock('../hooks/useSocialShare', () => ({
    useSocialShare: () => ({ shareToNetwork: mockShareToNetwork, isSharing: false }),
}));

describe('ShareModalComponent', () => {
    const mockProduct = {
        id: 1,
        name: 'Auto Test',
        description: 'Descripción de prueba',
        productImages: []
    };

    it('debe llamar a la función de compartir al hacer clic en un botón', async () => {
        const user = userEvent.setup();
        render(<ShareModalComponent product={mockProduct} onClose={vi.fn()} />);

        const whatsappBtn = screen.getByTitle('WhatsApp');
        await user.click(whatsappBtn);

        expect(mockShareToNetwork).toHaveBeenCalledWith('whatsapp', expect.any(String), expect.any(String));
    });
});
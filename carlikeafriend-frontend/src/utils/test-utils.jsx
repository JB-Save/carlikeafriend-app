import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { UserProvider } from '../context/UserProvider';
import { MessageModalProvider } from '../context/MessageModalContext';
import { FavoriteProvider } from '../context/FavoriteContext';
import { BookingProvider } from '../context/BookingContext';


// Proveedor global para los tests
const AllTheProviders = ({ children }) => {
    return (
        <BrowserRouter>
            <UserProvider value={{ user: { id: 1, name: 'Test User' }, token: 'fake-token', logout: vi.fn() }}>
                <MessageModalProvider value={{ showModal: false, modalMessage: '', setModalMessage: vi.fn() }}>
                    <FavoriteProvider>
                        <BookingProvider>
                            {children}
                        </BookingProvider>
                    </FavoriteProvider>
                </MessageModalProvider>
            </UserProvider>
        </BrowserRouter>
    );
};

const customRender = (ui, options) =>
    render(ui, { wrapper: AllTheProviders, ...options });

// Re-exportamos todo de testing-library y sobrescribimos render
export * from '@testing-library/react';
export { customRender as render };
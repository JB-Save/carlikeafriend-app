import userEvent from '@testing-library/user-event';
import { render, screen, waitFor } from '../utils/test-utils';
import { BookingSearchForm } from '../components/BookingSearchForm';
import { expect, describe, vi, it } from 'vitest';

// 1. Mocks estrictamente necesarios (navegación y utilidades externas)
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => mockNavigate
    };
});

// Mock parcial del BookingContext usando importOriginal
// Esto mantiene el BookingProvider real para test-utils, pero nos permite mockear useBooking
const mockUpdateBookingData = vi.fn();
vi.mock('../context/BookingContext', async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        useBooking: () => ({
            bookingData: {
                pickupBranch: null,
                returnBranch: null,
                differentReturnBranch: false,
                dateRange: [null, null],
                pickupTime: new Date('2026-08-18T10:00:00.000Z'),
                returnTime: new Date('2026-08-18T10:00:00.000Z')
            },
            updateBookingData: mockUpdateBookingData
        })
    };
});

// Mock específico para el DatePicker de terceros
vi.mock('react-datepicker', () => {
    return {
        __esModule: true,
        default: ({ onChange, id, ...props }) => (
            <input
                data-testid={id}
                onChange={(e) => {
                    if (props.selectsRange) {
                        onChange([new Date('2026-09-01'), new Date('2026-09-05')]);
                    } else {
                        onChange(new Date('2026-08-18T10:00:00.000Z'));
                    }
                }}
            />
        )
    };
});

// Mock de utilidades de fecha incluyendo getInitialPickupTime para el BookingProvider real
vi.mock('../utils/dateHelpers', () => ({
    isTimeOptionValidForPickup: vi.fn(() => true),
    isTimeOptionValidForReturn: vi.fn(() => true),
    getInitialPickupTime: vi.fn(() => new Date('2026-08-18T10:00:00.000Z'))
}));

const mockCitiesWithBranches = [
    {
        id: 1,
        name: 'Medellín',
        branches: [
            { id: 101, name: 'Aeropuerto JMC', address: 'Rionegro, Antioquia' },
            { id: 102, name: 'El Poblado', address: 'Calle 10' }
        ]
    },
    {
        id: 2,
        name: 'Bogotá',
        branches: [
            { id: 201, name: 'Aeropuerto El Dorado', address: 'Fontibón' }
        ]
    }
];

describe('BookingSearchForm Component', () => {
    const renderComponent = (props = {}) => {
        return render(
            <BookingSearchForm citiesWithBranches={mockCitiesWithBranches} {...props} />
        );
    };

    describe('Renderizado inicial y UI', () => {
        it('Debe renderizar los campos principales correctamente', () => {
            renderComponent();

            expect(screen.getByText('Busca tu Auto Ideal')).toBeInTheDocument();
            expect(screen.getByLabelText(/¿Dónde quieres recogerlo\?/i)).toBeInTheDocument();
            expect(screen.getByLabelText(/Devolver en otra sucursal/i)).toBeInTheDocument();
            expect(screen.getByText('Buscar')).toBeInTheDocument();
            expect(screen.queryByLabelText(/¿Dónde quieres devolverlo\?/i)).not.toBeInTheDocument();
        });
    });

    describe('Interacción del Autocompletado', () => {
        it('Debe mostrar sugerencias al escribir y permitir seleccionar una sucursal', async () => {
            const user = userEvent.setup();
            renderComponent();

            const pickupInput = screen.getByLabelText(/¿Dónde quieres recogerlo\?/i);
            await user.type(pickupInput, 'Med');

            await waitFor(() => {
                expect(screen.getByText('CIUDAD: MEDELLÍN')).toBeInTheDocument();
                expect(screen.getByText('Aeropuerto JMC')).toBeInTheDocument();
            });

            const branchOption = screen.getByText('Aeropuerto JMC');
            await user.click(branchOption);

            expect(pickupInput.value).toBe('Aeropuerto JMC - Medellín');

            await waitFor(() => {
                expect(screen.queryByText('CIUDAD: MEDELLÍN')).not.toBeInTheDocument();
            });
        });
    });

    describe('Lógica de devolución en otra sucursal', () => {
        it('Debe mostrar el input de devolución al activar el switch', async () => {
            const user = userEvent.setup();
            renderComponent();

            const switchInput = screen.getByLabelText(/Devolver en otra sucursal/i);

            // Activamos el switch
            await user.click(switchInput);

            await waitFor(() => {
                expect(screen.getByLabelText(/¿Dónde quieres devolverlo\?/i)).toBeInTheDocument();
            });
        });
    });

    describe('Sincronización Detail View', () => {
        it('En modo isDetailView, debe sincronizar cambios directamente con el contexto sin botón Buscar', async () => {
            const user = userEvent.setup();
            // Renderizamos en modo detalle
            renderComponent({ isDetailView: true });

            // Comprobamos que el título y el botón Buscar están ocultos por el isDetailView
            expect(screen.queryByText('Busca tu Auto Ideal')).not.toBeInTheDocument();
            expect(screen.queryByRole('button', { name: /Buscar/i })).not.toBeInTheDocument();

            const pickupInput = screen.getByLabelText(/¿Dónde quieres recogerlo\?/i);

            // Interacción: Buscar y seleccionar
            user.type(pickupInput, 'Bogotá');
            user.click(pickupInput);

            const branchOption = await screen.findByText('Aeropuerto El Dorado');
            user.click(branchOption);

            // Como está en isDetailView, el useEffect debería disparar updateBookingData automáticamente
            await waitFor(() => {
                expect(mockUpdateBookingData).toHaveBeenCalled();

                // Inspeccionamos los argumentos con los que se llamó
                const callArgs = mockUpdateBookingData.mock.calls[mockUpdateBookingData.mock.calls.length - 1][0];
                expect(callArgs.pickupBranch).toEqual(expect.objectContaining({
                    name: 'Aeropuerto El Dorado',
                    cityName: 'Bogotá'
                }));
            });
        });
    });

    describe('Validación y Errores', () => {
        it('Debe mostrar mensajes de error cuando el formulario está vacío al enviar', async () => {
            const user = userEvent.setup();
            renderComponent();
            const submitButton = screen.getByRole('button', { name: /Buscar/i });

            await user.click(submitButton);

            await waitFor(() => {
                expect(screen.getByText('Debes seleccionar una sucursal.')).toBeInTheDocument();
                expect(screen.getByText('Selecciona recogida y entrega.')).toBeInTheDocument();
            });
        });
    });

    describe('Envío del Formulario (Submit)', () => {
        // Función auxiliar para llenar el formulario y pasar la validación de Yup
        const fillRequiredFields = async (user) => {
            // 1. Sucursal
            const pickupInput = screen.getByLabelText(/¿Dónde quieres recogerlo\?/i);
            await user.type(pickupInput, 'Bogotá');

            const branchOption = await screen.findByText('Aeropuerto El Dorado');
            await user.click(branchOption);

            // 2. Fechas (usando el mock del DatePicker)
            const dateInput = screen.getByTestId('startAndEndDates');
            await user.type(dateInput, 'trigger');
        };

        it('Debe llamar a onSearchSubmit si se proporciona al hacer submit', async () => {
            const user = userEvent.setup();

            const mockOnSearchSubmit = vi.fn();
            renderComponent({ onSearchSubmit: mockOnSearchSubmit });

            await fillRequiredFields(user); // Llenamos datos para que Yup no bloquee el submit

            const submitButton = screen.getByRole('button', { name: /Buscar/i });

            await user.click(submitButton);


            await waitFor(() => {
                // Comprobamos que primero actualiza el estado global
                expect(mockUpdateBookingData).toHaveBeenCalled();
                // Y luego ejecuta el callback del componente padre
                expect(mockOnSearchSubmit).toHaveBeenCalled();
                // Verificamos que no navegó porque se pasó la prop onSearchSubmit
                expect(mockNavigate).not.toHaveBeenCalled();
            });
        });

        it('Debe navegar a /product-filter si no hay onSearchSubmit', async () => {
            const user = userEvent.setup();
            renderComponent(); // Sin prop onSearchSubmit

            await fillRequiredFields(user); // Llenamos datos para que Yup no bloquee el submit

            const submitButton = screen.getByRole('button', { name: /Buscar/i });

            await user.click(submitButton);

            await waitFor(() => {
                expect(mockUpdateBookingData).toHaveBeenCalled();
                expect(mockNavigate).toHaveBeenCalledWith('/product-filter');
            });
        });
    });

});
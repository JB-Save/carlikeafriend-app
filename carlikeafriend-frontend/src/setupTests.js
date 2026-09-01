import '@testing-library/jest-dom';
import { setupServer } from 'msw/node';
import { handlers } from './mocks/handlers';
import { afterAll, afterEach, beforeAll, vi } from 'vitest';

// Configuramos un servidor de peticiones con los handlers que creamos
export const server = setupServer(...handlers);

// Antes de que corran todos los tests, encendemos el servidor
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));

// Después de cada test, reseteamos cualquier handler que hayamos modificado en un test específico
// y limpiamos los mocks de funciones para evitar contaminación
afterEach(() => {
    server.resetHandlers();
    vi.clearAllMocks();
});

// Al terminar todos los tests, apagamos el servidor
afterAll(() => server.close());
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  test: {
    // Configura el entorno de prueba para simular un navegador
    // Esto resuelve el error "document is not defined"
    environment: 'jsdom',
    // Puedes agregar más opciones de Vitest aquí si es necesario
    globals: true, // Esto hace que las APIs como "describe", "test", "expect" estén disponibles globalmente
    setupFiles: './src/setupTests.js', // Asegúrate de tener este archivo si lo necesitas
  },
})

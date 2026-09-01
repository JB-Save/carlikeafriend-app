import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],

  /*build: {// <--- Para mantener el modelo unificado (archivos generados al ejecutar npm run build los copia dentro de la carpeta static en el backend )
    // __dirname representa la carpeta actual (carlikeafriend-frontend).
    // '../' sube un nivel hacia afuera.
    // Luego entra a la carpeta del backend y busca la ruta de los estáticos.
    outDir: path.resolve(__dirname, '../carlikeafriend-backend/src/main/resources/static'),
    emptyOutDir: true,
  },*/

  test: {
    // Configura el entorno de prueba para simular un navegador
    // Esto resuelve el error "document is not defined"
    environment: 'jsdom',
    // Puedes agregar más opciones de Vitest aquí si es necesario
    globals: true, // Esto hace que las APIs como "describe", "test", "expect" estén disponibles globalmente
    setupFiles: './src/setupTests.js', // Asegúrate de tener este archivo si lo necesitas
  },
})

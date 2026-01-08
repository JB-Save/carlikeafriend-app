import { useEffect } from "react";
import { useLocation } from "react-router-dom";

export const ScrollToTop = () => { //Este componente detecta cambios en la ruta y sube el scroll automáticamente.

    const { pathname } = useLocation();

    useEffect(() => {
        // Usamos setTimeout(0) para asegurar que se ejecuta justo después del renderizado del nuevo componente.
        const timer = setTimeout(() => {

            // 1. Intentar el elemento body/html directamente
            //behavior: 'instant'. Esto se utiliza para evitar que el navegador intente "suavizar" el scroll, asegurando que vaya al inicio de manera inmediata.
            document.documentElement.scrollTo({ top: 0, left: 0, behavior: 'instant' });
            document.body.scrollTo({ top: 0, left: 0, behavior: 'instant' });

            // 2. Intentar la ventana global (el comportamiento por defecto)
            window.scrollTo({ top: 0, left: 0, behavior: 'instant' });

            // 3. Intentar el elemento #root (si es el que tiene el scrollbar)
            const rootElement = document.getElementById('root');
            if (rootElement) {
                rootElement.scrollTo({ top: 0, left: 0, behavior: 'instant' });
            }

        }, 0); // 0ms de retraso

        return () => clearTimeout(timer); // Limpieza

    }, [pathname]);

    return null;
}

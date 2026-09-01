import { useContext, useState } from 'react';
import { API_CONFIG } from '../config/apiConfig';
import { UserContext } from '../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';

export const useSocialShare = (productId) => {

    const { token, logout } = useContext(UserContext);
    const [isSharing, setIsSharing] = useState(false);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const shareToNetwork = async (platform, customMessage, urlToShare) => {

        setIsSharing(true);

        // 1. URLs de las plataformas
        const encodedUrl = encodeURIComponent(urlToShare);
        const encodedMessage = encodeURIComponent(customMessage);

        const shareUrls = {
            facebook: `https://www.facebook.com/sharer/sharer.php?u=${encodedUrl}`,
            twitter: `https://twitter.com/intent/tweet?url=${encodedUrl}&text=${encodedMessage}`,
            whatsapp: `https://api.whatsapp.com/send?text=${encodedMessage}%20${encodedUrl}`,
            telegram: `https://t.me/share/url?url=${encodedUrl}&text=${encodedMessage}`,
            linkedin: `https://www.linkedin.com/sharing/share-offsite/?url=${encodedUrl}`
        };

        // 2. Abrir la ventana emergente
        if (shareUrls[platform]) {
            window.open(shareUrls[platform], '_blank', 'width=600,height=600,noopener,noreferrer');
        } else if (platform === 'copy') {
            try {
                // writeText devuelve una promesa, pero al iniciarla inmediatamente tras el clic, el navegador la permite.
                navigator.clipboard.writeText(`${customMessage} ${urlToShare}`);
            } catch (err) {
                console.error("El navegador bloqueó el acceso al portapapeles:", err);
            }
        }

        await logShareInteraction(platform, customMessage);
        setIsSharing(false);
    };

    // Función 2: Para registrar silenciosamente la interacción nativa (Móvil)
    const logShareInteraction = async (platform, customMessage) => {
        try {

            const response = await fetch(API_CONFIG.SHARE, {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    productId: productId,
                    platform: platform,
                    customMessage: customMessage
                })
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

            if (!response.ok) {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }

        } catch (error) {
            console.error("No se pudo registrar la métrica de compartir:", error);
        }
    };

    return { shareToNetwork, logShareInteraction, isSharing };
}

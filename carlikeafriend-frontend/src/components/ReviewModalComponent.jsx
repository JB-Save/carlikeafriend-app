import React, { useState, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useNavigate } from 'react-router-dom';

export const ReviewModalComponent = ({ productId, onClose, onSuccess }) => {
    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const navigate = useNavigate();

    const [stars, setStars] = useState(0);
    const [hover, setHover] = useState(0);
    const [comment, setComment] = useState("");
    const [emptyStarMsg, setEmptyStarMsg] = useState(null)
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (stars === 0) {
            setEmptyStarMsg("Por favor, selecciona una puntuación de 1 a 5 estrellas.");
            return;
        }

        setIsSubmitting(true);

        try {

            const response = await fetch(`${API_CONFIG.PRODUCT_REVIEWS}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ productId, stars, comment })
            });

            if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

            if (response.ok) {
                setModalMessage("¡Gracias por tu valoración!");
                if (onSuccess) onSuccess();
                onClose();
            } else {
                const errorMsg = await extractErrorMessage(response);
                setModalMessage(errorMsg || "No pudimos guardar tu reseña. Recuerda que debes haber completado una reserva de este vehículo.");
                onClose();
            }
        } catch (error) {
            console.log("No se pudo guardar reseña: " + error);
            setModalMessage("Error de conexión al enviar la reseña.");
            onClose();
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)' }} tabIndex="-1" role="dialog">
            <div className="modal-dialog modal-dialog-centered" role="document">
                <div className="modal-content rounded-4 border-0 shadow">
                    <div className="modal-header border-bottom-0 pb-0">
                        <h5 className="modal-title fw-bold" style={{ color: '#2e2e84' }}>Calificar Vehículo</h5>
                        <button type="button" className="btn-close" onClick={onClose} aria-label="Close"></button>
                    </div>

                    <div className="modal-body p-4">
                        <form onSubmit={handleSubmit}>
                            {/* Selector de Estrellas Interactivo[cite: 17] */}
                            <div className="mb-4 d-flex flex-column align-items-center">
                                <span className="mb-2 fw-medium" style={{ color: '#1f88e6' }}>¿Cómo fue tu experiencia?</span>
                                <div>
                                    {[...Array(5)].map((star, index) => {
                                        index += 1;
                                        return (
                                            <i
                                                key={index}
                                                className={`bi display-6 mx-1 ${index <= (hover || stars) ? "bi-star-fill text-warning" : "bi-star text-secondary"}`}
                                                style={{ cursor: "pointer", transition: "color 200ms" }}
                                                onClick={() => setStars(index)}
                                                onMouseEnter={() => setHover(index)}
                                                onMouseLeave={() => setHover(stars)}
                                            ></i>
                                        );
                                    })}
                                </div>
                            </div>

                            <div className="mb-4">
                                <textarea
                                    className="form-control"
                                    rows="3"
                                    placeholder="Comparte detalles de tu experiencia con este vehículo (opcional)"
                                    value={comment}
                                    maxLength="500"
                                    onChange={(e) => setComment(e.target.value)}
                                ></textarea>
                                <small className="d-block mt-1 text-end" style={{ color: '#6a5e9b' }}>
                                    {comment.length}/500
                                </small>
                            </div>

                            {emptyStarMsg && (
                                <p className="text-danger fw-bold text-justify small">
                                    {emptyStarMsg}
                                </p>
                            )}

                            <style>{`
                                .btn-custom-modal {
                                    background-color: #1f88e6;
                                    color: #f4f3f2;
                                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                                }
                                .btn-custom-modal:hover:not(:disabled) {
                                    background-color: #2e2e84;
                                    color: #f4f3f2;
                                    transform: translateY(-2px);
                                    box-shadow: 0 8px 20px rgba(46, 46, 132, 0.25);
                                }
                            `}</style>

                            <div className="d-flex justify-content-end gap-2">
                                <button type="button" className="btn btn-light rounded-pill px-4 fw-bold" onClick={onClose} disabled={isSubmitting}>
                                    Cancelar
                                </button>
                                <button type="submit" className="btn btn-custom-modal rounded-pill px-4 fw-bold" disabled={isSubmitting}>
                                    {isSubmitting ? 'Enviando...' : 'Publicar Reseña'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};
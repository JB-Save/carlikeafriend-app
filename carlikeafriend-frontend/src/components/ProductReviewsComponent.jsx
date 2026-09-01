import { useState, useEffect, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { API_CONFIG } from '../config/apiConfig';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useMessageModal } from '../context/MessageModalContext';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useNavigate } from 'react-router-dom';
import { adaptStringToUserObject, getAvatarColor, getFormattedName, getInitials } from '../utils/stringHelpers';

export const ProductReviewsComponent = ({ productId, onReviewAdded }) => {
    const { token, logout, isAuthenticated } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();

    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(false);

    // Estados del formulario
    const [stars, setStars] = useState(0);
    const [hover, setHover] = useState(0);
    const [comment, setComment] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        fetchReviews();
    }, [productId]);

    const fetchReviews = async () => {
        setLoading(true);

        try {
            const response = await fetch(`${API_CONFIG.PRODUCT_REVIEWS}/${productId}/products`, {
                method: 'GET'
            });

            if (response.ok) {
                const data = await response.json();
                setReviews(data);
            } else {
                // Manejo de otros errores (400, 500, etc.)
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error cargando reseñas:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (stars === 0) {
            setModalMessage("Por favor, selecciona una puntuación de 1 a 5 estrellas.");
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
                const newReview = await response.json();
                setReviews([newReview, ...reviews]); // Añadir al inicio de la lista
                setStars(0);
                setComment("");
                if (onReviewAdded) await onReviewAdded(); // Disparar actualización de stats en el padre
                setModalMessage("¡Gracias por tu valoración!");
            } else {
                const errorMsg = await extractErrorMessage(response);
                setModalMessage(errorMsg || "No pudimos guardar tu reseña. Recuerda que debes haber completado una reserva de este vehículo.");
            }
        } catch (error) {
            setModalMessage("Error de conexión al enviar la reseña.");
        } finally {
            setIsSubmitting(false);
        }
    };

    // Formatear fecha
    const formatDate = (dateString) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('es-CO', options);
    };

    return (
        <div className="container mt-5 py-4 border-top">
            <h3 className="fw-bold mb-4 fs-4" style={{ color: '#2e2e84' }}>
                Valoraciones de los usuarios
            </h3>

            {/* FORMULARIO DE RESEÑA (Solo usuarios autenticados) */}
            {isAuthenticated ? (
                <div className="bg-light p-4 rounded-4 mb-5 border shadow-sm">
                    <h5 className="fw-bold mb-3" style={{ color: '#2e2e84' }}>Deja tu opinión</h5>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3 d-flex align-items-center">
                            <span className="me-3 fw-medium" style={{ color: '#1f88e6' }}>Tu puntuación:</span>
                            {[...Array(5)].map((star, index) => {
                                index += 1;
                                return (
                                    <i
                                        key={index}
                                        className={`bi fs-4 ${index <= (hover || stars) ? "bi-star-fill text-warning" : "bi-star text-secondary"}`}
                                        style={{ cursor: "pointer", transition: "color 200ms" }}
                                        onClick={() => setStars(index)}
                                        onMouseEnter={() => setHover(index)}
                                        onMouseLeave={() => setHover(stars)}
                                    ></i>
                                );
                            })}
                        </div>
                        <div className="mb-3">
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
                        <style>{`
                                .btn-custom {
                                                background-color: #1f88e6;
                                                color: #f4f3f2;
                                                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                                            }
                                .btn-custom:hover:not(:disabled) {
                                                background-color: #2e2e84;
                                                color: #f4f3f2;
                                                transform: translateY(-2px);
                                                box-shadow: 0 8px 20px rgba(46, 46, 132, 0.25);
                                            }
                                `}
                        </style>
                        <button
                            type="submit"
                            className="btn btn-custom rounded-pill px-4 fw-bold"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Enviando...' : 'Publicar Reseña'}
                        </button>
                    </form>
                </div>
            ) : (
                <div className="alert text-center mb-5 rounded-4 border-0 bg-light" style={{ color: '#70ACDE' }}>
                    <i className="bi bi-lock-fill me-2"></i>
                    Inicia sesión y completa una reserva para dejar tu valoración.
                </div>
            )}

            {/* LISTADO DE RESEÑAS */}
            {loading ? (
                <div className="text-center py-4"><div className="spinner-border"></div></div>
            ) : reviews.length === 0 ? (
                <p className="text-center py-4" style={{ color: '#6a5e9b' }}>Aún no hay valoraciones para este vehículo. ¡Sé el primero en opinar!</p>
            ) : (
                <div className="row g-4">
                    {reviews.map((review) => {
                        const userObject = adaptStringToUserObject(review?.user?.name);
                        const displayName = getFormattedName(userObject)
                        const initials = getInitials(displayName !== 'Usuario Anónimo' ? displayName : '');
                        const avatarBgColor = getAvatarColor(initials);
                        return (
                            <div className="col-12 col-md-6" key={review.id}>
                                <div className="card h-100 border-0 shadow-sm rounded-4 p-3 bg-white">
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <div className="d-flex align-items-center justify-content-center">
                                                <div className="d-flex align-items-center justify-content-center rounded-circle text-white fw-bold py-2 px-2"
                                                    style={{
                                                        width: '35px',
                                                        height: '35px',
                                                        fontSize: '0.9rem',
                                                        backgroundColor: avatarBgColor
                                                    }}
                                                >
                                                    {initials}
                                                </div>
                                                <div className="ms-2 small text-break fw-bold mb-1">{displayName}</div>
                                            </div>
                                            <div className="small" style={{ color: '#6a5e9b' }}>
                                                {formatDate(review.createdAt)}
                                            </div>
                                        </div>
                                        <div className="d-flex bg-warning-subtle px-2 py-1 rounded-pill">
                                            <i className="bi bi-star-fill text-warning me-1 small"></i>
                                            <span className="fw-bold text-warning-emphasis small">{review.stars}</span>
                                        </div>
                                    </div>
                                    {review.comment && (
                                        <p className="card-text text-secondary small mt-2 mb-0" style={{ fontStyle: 'italic' }}>
                                            "{review.comment}"
                                        </p>
                                    )}
                                </div>
                            </div>
                        )
                    })}
                </div>
            )}
        </div>
    );
}

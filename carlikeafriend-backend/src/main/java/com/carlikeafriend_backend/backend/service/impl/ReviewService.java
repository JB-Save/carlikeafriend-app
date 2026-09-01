package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.ReviewDTO;
import com.carlikeafriend_backend.backend.dto.ReviewResponseDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.ResourceNotAvailableException;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService implements IReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final IReviewRepository reviewRepository;
    private final IReservationRepository reservationRepository;
    private final IProductRepository productRepository;


    @Autowired
    public ReviewService(IReviewRepository reviewRepository, IReservationRepository reservationRepository, IProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ReviewResponseDTO saveReview(Long userId, ReviewDTO reviewDTO) {

        logger.info("Intentando crar una reseña para el vehículo con ID: {}", reviewDTO.getProductId());

        // 1. Buscar la última reserva completada del usuario para este PRODUCTO
        Reservation reservation = reservationRepository
                .findLatestCompletedReservationByUserAndProduct(userId, reviewDTO.getProductId(), ReservationStatus.COMPLETED)
                .orElseThrow(() -> {
                    logger.warn("El usuario {} no tiene reservas completadas para el producto {}", userId, reviewDTO.getProductId());
                    return new ResourceNotAvailableException("Debes haber completado una reserva de este vehículo para poder calificarlo.");
                });

        // 2. Validar que la reserva ya no haya sido calificada
        if (reviewRepository.existsByReservationId(reservation.getId())) {
            logger.warn("La reserva ID {} ya tiene una reseña asociada.", reservation.getId());
            throw new ResourceNotAvailableException("Ya has enviado una valoración para tu última reserva de este vehículo.");
        }

        // 3. Extraer el vehículo específico que realmente usó
        Vehicle vehicle = reservation.getVehicle();
        User user = reservation.getUser();

        // 4. Persistencia de la Review
        Review review = new Review();
        review.setStars(reviewDTO.getStars());
        review.setComment(reviewDTO.getComment());
        review.setReservation(reservation);

        user.addReview(review);
        vehicle.addReview(review);

        Review savedReview = reviewRepository.save(review);

        // 5. Actualizar el promedio en el Modelo asociado al vehículo
        Product product = vehicle.getProduct();
        refreshProductStats(product); // Método privado de apoyo
        productRepository.save(product);

        logger.info("Reseña guardada exitosamente con ID: {} para la reserva ID: {}", savedReview.getId(), reservation.getId());
        return mapToReviewDto(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        logger.info("Buscando reseñas.");
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToReviewDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToReviewDto(Review review) {
        SimpleResponseDTO user = review.getUser() != null
                ? new SimpleResponseDTO(review.getUser().getId(), review.getUser().getName() + " " + review.getUser().getLastName())
                : null;
        SimpleResponseDTO product = review.getVehicle().getProduct() != null
                ? new SimpleResponseDTO(review.getVehicle().getProduct().getId(), review.getVehicle().getProduct().getName())
                : null;

        return new ReviewResponseDTO(
                review.getId(),
                user,
                product,
                review.getStars(),
                review.getComment(),
                review.getCreatedAt().toString()
        );
    }

    private void refreshProductStats(Product product) {
        List<Object[]> result = reviewRepository.getRatingStatsByProductId(product.getId());

        if (result != null && !result.isEmpty() && result.get(0) != null) {
            Object[] stats = result.get(0);
            // stats[0] es el promedio (Double), stats[1] es el conteo (Long)
            Double avg = (Double) stats[0];
            Long count = (Long) stats[1];
            // Actualizamos el producto con los datos reales de la BD
            product.setAverageRating(avg != null ? avg : 0.0);
            product.setTotalReviews(count != null ? count.intValue() : 0);
        } else {
            // Si no se encuentra nada, reiniciamos a valores por defecto
            product.setAverageRating(0.0);
            product.setTotalReviews(0);
        }


    }
}

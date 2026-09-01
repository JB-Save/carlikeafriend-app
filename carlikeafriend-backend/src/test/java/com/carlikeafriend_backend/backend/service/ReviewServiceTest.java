package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ReviewDTO;
import com.carlikeafriend_backend.backend.dto.ReviewResponseDTO;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.ResourceNotAvailableException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.repository.IReservationRepository;
import com.carlikeafriend_backend.backend.repository.IReviewRepository;
import com.carlikeafriend_backend.backend.service.impl.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private IReviewRepository reviewRepository;

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("saveReview - Crea reseña y actualiza estadísticas del producto")
    void saveReview_Success() {
        Long userId = 1L;
        ReviewDTO dto = new ReviewDTO();
        dto.setProductId(10L);
        dto.setStars(5);
        dto.setComment("Excelente vehículo");

        User user = new User();
        user.setId(userId);
        user.setName("Juan");
        user.setLastName("Perez");

        Product product = new Product();
        product.setId(10L);
        product.setName("Toyota Corolla");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setProduct(product);

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUser(user);
        reservation.setVehicle(vehicle);

        Review reviewToSave = new Review();
        reviewToSave.setId(100L);
        reviewToSave.setUser(user);
        reviewToSave.setVehicle(vehicle);
        reviewToSave.setStars(5);
        reviewToSave.setComment("Excelente vehículo");
        reviewToSave.setReservation(reservation);
        reviewToSave.setCreatedAt(LocalDateTime.now());

        when(reservationRepository.findLatestCompletedReservationByUserAndProduct(userId, 10L, ReservationStatus.COMPLETED))
                .thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservationId(reservation.getId())).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewToSave);
        when(reviewRepository.getRatingStatsByProductId(10L)).thenReturn(List.<Object[]>of(new Object[]{4.5, 2L}));

        ReviewResponseDTO result = reviewService.saveReview(userId, dto);

        assertNotNull(result);
        assertEquals(5, result.getStars());

        // Verificar que se recalcularon los stats del producto y se guardó
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertEquals(4.5, productCaptor.getValue().getAverageRating());
        assertEquals(2, productCaptor.getValue().getTotalReviews());
    }

    @Test
    @DisplayName("saveReview - Lanza excepción si la reserva ya fue calificada previamente")
    void saveReview_AlreadyReviewed_ThrowsException() {
        Long userId = 1L;
        ReviewDTO dto = new ReviewDTO();
        dto.setProductId(10L);
        dto.setStars(5);
        dto.setComment("Comentario");

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());

        when(reservationRepository.findLatestCompletedReservationByUserAndProduct(userId, 10L, ReservationStatus.COMPLETED))
                .thenReturn(Optional.of(reservation));
        when(reviewRepository.existsByReservationId(reservation.getId())).thenReturn(true);

        assertThrows(ResourceNotAvailableException.class, () -> reviewService.saveReview(userId, dto));
    }

    @Test
    @DisplayName("getReviewsByProduct - Retorna la lista de reseñas asociadas")
    void getReviewsByProduct_Success() {
        Review review = new Review();
        review.setId(1L);
        review.setStars(5);
        review.setComment("Muy bueno");
        review.setCreatedAt(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();
        vehicle.setProduct(new Product());
        review.setVehicle(vehicle);

        when(reviewRepository.findByProductId(10L)).thenReturn(List.of(review));

        List<ReviewResponseDTO> result = reviewService.getReviewsByProduct(10L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getStars());
    }
}
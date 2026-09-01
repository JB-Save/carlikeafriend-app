package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ReviewDTO;
import com.carlikeafriend_backend.backend.dto.ReviewResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@WithMockUser
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IReviewService reviewService;

    @MockitoBean
    private IJwtService jwtService;

    @Test
    @DisplayName("POST /reviews - Crear nueva reseña")
    void saveReview_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, null);

        ReviewDTO dto = new ReviewDTO();
        dto.setProductId(10L);
        dto.setStars(5);
        dto.setComment("Excelente experiencia");
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(100L, null, null, 5, "Excelente experiencia", "2026-08-01T10:00:00");

        when(reviewService.saveReview(eq(1L), any(ReviewDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/carlikeafriend/reviews")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.stars").value(5));
    }

    @Test
    @DisplayName("GET /reviews/{productId}/products - Obtener reseñas de un producto")
    void getProductReviews_Success() throws Exception {
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(100L, null, null, 5, "Excelente experiencia", "2026-08-01T10:00:00");
        when(reviewService.getReviewsByProduct(10L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/carlikeafriend/reviews/10/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }
}
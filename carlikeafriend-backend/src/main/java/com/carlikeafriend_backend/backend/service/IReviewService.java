package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ReviewDTO;
import com.carlikeafriend_backend.backend.dto.ReviewResponseDTO;

import java.util.List;

public interface IReviewService {

    ReviewResponseDTO saveReview(Long userId, ReviewDTO reviewDTO);
    List<ReviewResponseDTO> getReviewsByProduct(Long productId);
}

package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ReviewDTO;
import com.carlikeafriend_backend.backend.dto.ReviewResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("carlikeafriend")
public class ReviewController {

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDTO> saveReview(@RequestBody @Valid ReviewDTO reviewDTO,
                                                        @AuthenticationPrincipal User currentUser) {
        ReviewResponseDTO review = reviewService.saveReview(currentUser.getId(), reviewDTO);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/reviews/{productId}/products")
    public ResponseEntity<List<ReviewResponseDTO>> getProductReviews(@PathVariable Long productId) {
        return new ResponseEntity<>(reviewService.getReviewsByProduct(productId), HttpStatus.OK);
    }
}

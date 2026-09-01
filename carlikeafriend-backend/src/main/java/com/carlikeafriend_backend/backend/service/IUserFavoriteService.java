package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.UserFavoriteDTO;
import com.carlikeafriend_backend.backend.dto.UserFavoriteResponseDTO;

import java.util.List;

public interface IUserFavoriteService {
    void manageFavorite(Long userId, Long productId);
    List<UserFavoriteResponseDTO> findAllFavoriteProductsByUserId(Long userId);
    void removeFavoriteFromUser(Long userId, Long productId);
    void resetUserFavorites(Long userId);

}

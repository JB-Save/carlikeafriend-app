package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.UserFavoriteDTO;
import com.carlikeafriend_backend.backend.dto.UserFavoriteResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IUserFavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carlikeafriend/products")
public class UserFavoriteController {

    private final IUserFavoriteService favoriteService;

    @Autowired
    public UserFavoriteController(IUserFavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 1. Marcar o desmarcar (Toggle)
    @PostMapping("/favorites")
    public ResponseEntity<String> toggleFavorite(@RequestBody @Valid UserFavoriteDTO favoriteDTO,
                                                 @AuthenticationPrincipal User currentUser) {
        favoriteService.manageFavorite(
                currentUser.getId(),
                favoriteDTO.getProductId());
        return new ResponseEntity<>("Operación realizada con éxito", HttpStatus.CREATED);
    }

    // 2. Obtener lista de "Mis Favoritos"
    @GetMapping("/favorites/me")
    public ResponseEntity<List<UserFavoriteResponseDTO>> getMyFavorites(@AuthenticationPrincipal User currentUser) {
      return new ResponseEntity<>(favoriteService.findAllFavoriteProductsByUserId(currentUser.getId()), HttpStatus.OK);
    }

    // 3. Eliminar específicamente desde la lista de "Mi Cuenta"
    @DeleteMapping("/favorites/{productId}/me")
    public ResponseEntity<Void> removeFavorite(@AuthenticationPrincipal User currentUser, @PathVariable Long productId) {
        favoriteService.removeFavoriteFromUser(currentUser.getId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 4. Eliminar toda la lista desde de "Mi Cuenta"
    @DeleteMapping("/favorites/me")
    public ResponseEntity<Void> removeAllFavorite(@AuthenticationPrincipal User currentUser) {
        favoriteService.resetUserFavorites(currentUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

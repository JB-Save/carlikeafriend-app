package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.UserFavoriteResponseDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.repository.IUserFavoriteRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.impl.UserFavoriteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFavoriteServiceTest {

    @Mock
    private IUserFavoriteRepository favoriteRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private UserFavoriteService favoriteService;

    @Test
    @DisplayName("manageFavorite - Si ya existe como favorito, debe desmarcarlo y eliminarlo")
    void manageFavorite_WhenExists_ShouldDelete() {
        when(favoriteRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(true);

        favoriteService.manageFavorite(1L, 10L);

        verify(favoriteRepository).deleteByUserIdAndProductId(1L, 10L);
        verify(userRepository, never()).findByIdAndDeletedFalse(anyLong());
    }

    @Test
    @DisplayName("manageFavorite - Si no existe como favorito, debe agregarlo al usuario")
    void manageFavorite_WhenNotExists_ShouldAdd() {
        User mockUser = spy(new User());
        mockUser.setId(1L);
        Product mockProduct = new Product();
        mockProduct.setId(10L);

        when(favoriteRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(false);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.getReferenceById(10L)).thenReturn(mockProduct);

        favoriteService.manageFavorite(1L, 10L);

        verify(mockUser).addFavorite(mockProduct);
    }

    @Test
    @DisplayName("findAllFavoriteProductsByUserId - Retorna la lista mapeada a DTOs")
    void findAllFavoriteProductsByUserId_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        Product mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setName("Toyota Corolla");

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
        when(favoriteRepository.findAllFavoriteProductsByUserId(1L)).thenReturn(List.of(mockProduct));

        List<UserFavoriteResponseDTO> result = favoriteService.findAllFavoriteProductsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Toyota Corolla", result.get(0).getName());
    }

    @Test
    @DisplayName("removeFavoriteFromUser - Remueve el producto de la colección del usuario")
    void removeFavoriteFromUser_Success() {
        User mockUser = spy(new User());
        mockUser.setId(1L);
        Product mockProduct = new Product();
        mockProduct.setId(10L);

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));
        when(productRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(mockProduct));

        favoriteService.removeFavoriteFromUser(1L, 10L);

        verify(mockUser).removeFavorite(mockProduct);
    }

    @Test
    @DisplayName("resetUserFavorites - Limpia todos los favoritos del usuario")
    void resetUserFavorites_Success() {
        User mockUser = spy(new User());
        mockUser.setId(1L);

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));

        favoriteService.resetUserFavorites(1L);

        verify(mockUser).clearAllFavorites();
    }
}

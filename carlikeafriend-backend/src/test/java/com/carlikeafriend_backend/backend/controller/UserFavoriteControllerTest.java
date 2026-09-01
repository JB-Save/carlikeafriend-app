package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.UserFavoriteDTO;
import com.carlikeafriend_backend.backend.dto.UserFavoriteResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IUserFavoriteService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserFavoriteController.class)
@WithMockUser
public class UserFavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserFavoriteService favoriteService;

    @MockitoBean
    private IJwtService jwtService;

    private UsernamePasswordAuthenticationToken createAuthUser() {
        User user = new User();
        user.setId(1L);
        return new UsernamePasswordAuthenticationToken(user, null, null);
    }

    @Test
    @DisplayName("POST /products/favorites - Alternar (toggle) producto favorito")
    void toggleFavorite_Success() throws Exception {
        UserFavoriteDTO dto = new UserFavoriteDTO();
        dto.setProductId(10L);
        doNothing().when(favoriteService).manageFavorite(1L, 10L);

        mockMvc.perform(post("/carlikeafriend/products/favorites")
                        .with(csrf())
                        .with(authentication(createAuthUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Operación realizada con éxito"));
    }

    @Test
    @DisplayName("GET /products/favorites/me - Obtener mis favoritos")
    void getMyFavorites_Success() throws Exception {
        UserFavoriteResponseDTO item = new UserFavoriteResponseDTO();
        item.setId(10L);
        item.setName("Toyota Corolla");

        when(favoriteService.findAllFavoriteProductsByUserId(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/carlikeafriend/products/favorites/me")
                        .with(authentication(createAuthUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Toyota Corolla"));
    }

    @Test
    @DisplayName("DELETE /products/favorites/{productId}/me - Eliminar un favorito")
    void removeFavorite_Success() throws Exception {
        doNothing().when(favoriteService).removeFavoriteFromUser(1L, 10L);

        mockMvc.perform(delete("/carlikeafriend/products/favorites/10/me")
                        .with(csrf())
                        .with(authentication(createAuthUser())))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/favorites/me - Reiniciar todos los favoritos")
    void removeAllFavorite_Success() throws Exception {
        doNothing().when(favoriteService).resetUserFavorites(1L);

        mockMvc.perform(delete("/carlikeafriend/products/favorites/me")
                        .with(csrf())
                        .with(authentication(createAuthUser())))
                .andExpect(status().isNoContent());
    }
}
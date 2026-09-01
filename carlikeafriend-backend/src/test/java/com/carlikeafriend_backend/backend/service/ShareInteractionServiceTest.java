package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ShareInteractionDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.ShareInteraction;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.repository.IShareInteractionRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.impl.ShareInteractionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShareInteractionServiceTest {

    @Mock
    private IShareInteractionRepository interactionRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ShareInteractionService interactionService;

    @Test
    @DisplayName("saveInteraction - Guarda correctamente la interacción capturando los valores de la entidad")
    void saveInteraction_Success() {
        Long userId = 1L;
        ShareInteractionDTO dto = new ShareInteractionDTO();
        dto.setProductId(10L);
        dto.setPlatform( "WhatsApp");
        dto.setCustomMessage("¡Mira este auto!");

        User mockUser = spy(new User());
        Product mockProduct = spy(new Product());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(mockProduct));

        interactionService.saveInteraction(userId, dto);

        ArgumentCaptor<ShareInteraction> captor = ArgumentCaptor.forClass(ShareInteraction.class);
        verify(interactionRepository).save(captor.capture());

        ShareInteraction captured = captor.getValue();
        assertEquals("WhatsApp", captured.getPlatform());
        assertEquals("¡Mira este auto!", captured.getCustomMessage());
        verify(mockUser).addShareInteraction(captured);
        verify(mockProduct).addShareInteraction(captured);
    }

    @Test
    @DisplayName("saveInteraction - Lanza ResourceNotFoundException si el usuario no existe")
    void saveInteraction_UserNotFound_ThrowsException() {
        ShareInteractionDTO dto = new ShareInteractionDTO();
        dto.setProductId(10L);
        dto.setPlatform( "WhatsApp");
        dto.setCustomMessage("Mensaje");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> interactionService.saveInteraction(1L, dto));
    }
}
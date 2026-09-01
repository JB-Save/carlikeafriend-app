package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.ShareInteractionDTO;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.ShareInteraction;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.repository.IShareInteractionRepository;
import com.carlikeafriend_backend.backend.service.IShareInteractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShareInteractionService implements IShareInteractionService {

    private static final Logger logger = LoggerFactory.getLogger(ShareInteractionService.class);

    private final IShareInteractionRepository interactionRepository;
    private final IUserRepository userRepository;
    private final IProductRepository productRepository;

    @Autowired
    public ShareInteractionService(IShareInteractionRepository interactionRepository, IUserRepository userRepository, IProductRepository productRepository) {
        this.interactionRepository = interactionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Async("shareInteractionExecutor")
    @Transactional
    public void saveInteraction(Long userId, ShareInteractionDTO interactionDTO) {

        logger.info("Intentando guardar log de interacción redes sociales - producto ID: {}", interactionDTO.getProductId());

        // Simplificamos la obtención de referencias
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        Product product = productRepository.findById(interactionDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + interactionDTO.getProductId()));

        ShareInteraction interaction = new ShareInteraction();
        interaction.setPlatform(interactionDTO.getPlatform());
        interaction.setCustomMessage(interactionDTO.getCustomMessage());

        // Aquí seteamos directamente las relaciones
        user.addShareInteraction(interaction);
        product.addShareInteraction(interaction);

        interactionRepository.save(interaction);

    }
}

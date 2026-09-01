package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.FeatureResponseDTO;
import com.carlikeafriend_backend.backend.dto.ImageDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.dto.UserFavoriteResponseDTO;
import com.carlikeafriend_backend.backend.entity.Feature;
import com.carlikeafriend_backend.backend.entity.Product;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IProductRepository;
import com.carlikeafriend_backend.backend.repository.IUserFavoriteRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.IUserFavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService implements IUserFavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteService.class);

    private final IUserFavoriteRepository favoriteRepository;
    private final IUserRepository userRepository;
    private final IProductRepository productRepository;

    @Autowired
    public UserFavoriteService(IUserFavoriteRepository favoriteRepository, IUserRepository userRepository, IProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    @Override
    @Transactional
    public void manageFavorite(Long userId, Long productId) {

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            logger.info("Intentando desmarcar un producto como favorito con ID: {}", productId);
            // Acción: Desmarcar (Uso del Repo para eficiencia)
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        } else {
            logger.info("Intentando marcar un producto como favorito con ID: {}", productId);
            // Acción: Marcar (Uso de métodos de conveniencia para integridad de objetos)
            User user = userRepository.findByIdAndDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
            Product product = productRepository.getReferenceById(productId);

            user.addFavorite(product);

        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFavoriteResponseDTO> findAllFavoriteProductsByUserId(Long userId) {
        logger.info("Buscando lista de favoritos.");
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        List<Product> favoriteProductsByUserId = favoriteRepository.findAllFavoriteProductsByUserId(user.getId());

        return favoriteProductsByUserId.stream()
                .map(this::mapToUserFavoriteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFavoriteFromUser(Long userId, Long productId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        Product product = productRepository.findByIdAndDeletedFalse(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        // Usamos el método de conveniencia para mantener la integridad en memoria
        user.removeFavorite(product);
        logger.warn("Favorito eliminado de la lista de usuario con ID: {}", productId);

    }

    @Override
    @Transactional
    public void resetUserFavorites(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        user.clearAllFavorites();
        logger.warn("Todos los Favoritos eliminados de la lista de usuario.");
    }

    private UserFavoriteResponseDTO mapToUserFavoriteDto(Product product) {

        List<ImageDTO> imageDtos = product.getImages() != null
                ? product.getImages().stream().map(i -> new ImageDTO(i.getId(), i.getImagePath(), i.getOriginalName(), i.getContentType())).collect(Collectors.toList())
                : new ArrayList<>();

        SimpleResponseDTO makeDto = product.getMake() != null
                ? new SimpleResponseDTO(product.getMake().getId(), product.getMake().getName())
                : null;

        List<SimpleResponseDTO> categoryDtos = product.getCategories() != null
                ? product.getCategories().stream().map(c -> new SimpleResponseDTO(c.getId(), c.getName())).collect(Collectors.toList())
                : new ArrayList<>();

        List<FeatureResponseDTO> featureDtos = new ArrayList<>();
        if (product.getFeatures() != null) {
            for (Feature f : product.getFeatures()) {
                ImageDTO icon = f.getIcon() != null
                        ? new ImageDTO(f.getIcon().getId(), f.getIcon().getImagePath(), f.getIcon().getOriginalName(), f.getIcon().getContentType())
                        : null;
                featureDtos.add(new FeatureResponseDTO(
                        f.getId(),
                        f.getName(),
                        icon
                ));
            }
        }

        return new UserFavoriteResponseDTO(
                product.getId(),
                product.getName(),
                makeDto,
                categoryDtos,
                featureDtos,
                imageDtos,
                product.getPrice(),
                product.getAverageRating(),
                product.getTotalReviews(),
                product.getPassengerCapacity(),
                product.getBaggageCapacity(),
                product.getNumberOfDoors()
        );
    }
}

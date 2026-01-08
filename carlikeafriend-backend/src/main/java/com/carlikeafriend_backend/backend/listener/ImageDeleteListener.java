package com.carlikeafriend_backend.backend.listener;

import com.carlikeafriend_backend.backend.event.ImageDeletedEvent;
import com.carlikeafriend_backend.backend.service.IFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;

@Component
public class ImageDeleteListener {

    private static final Logger logger = LoggerFactory.getLogger(ImageDeleteListener.class);
    private final IFileStorageService fileStorageService;

    @Autowired
    public ImageDeleteListener(IFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Esta anotación solo se ejecuta si el commit de la DB fue EXITOSO.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageDeletion(ImageDeletedEvent event) {
        try {
            logger.info("Transacción confirmada. Eliminando archivo físico: {}", event.getImagePath());
            fileStorageService.deleteFile(event.getImagePath());
        } catch (IOException e) {
            // Aquí la DB ya se actualizó, así que solo podemos loguear el error.
            logger.error("Error al eliminar archivo físico {} tras commit de DB", event.getImagePath(), e);
        }
    }
}

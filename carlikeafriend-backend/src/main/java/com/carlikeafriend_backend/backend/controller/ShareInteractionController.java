package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ShareInteractionDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IShareInteractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("carlikeafriend")
public class ShareInteractionController {

    private final IShareInteractionService interactionService;

    @Autowired
    public ShareInteractionController(IShareInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/share")
    public ResponseEntity<String> saveInteraction(@RequestBody @Valid ShareInteractionDTO interactionDTO,
                                                  @AuthenticationPrincipal User currentUser){
        interactionService.saveInteraction(currentUser.getId(), interactionDTO);
        return new ResponseEntity<>("Registro exitoso de interacción en redes sociales", HttpStatus.CREATED);
    }
}

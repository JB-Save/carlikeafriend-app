package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ShareInteractionDTO;

public interface IShareInteractionService {
    void saveInteraction(Long userId, ShareInteractionDTO interactionDTO);
}

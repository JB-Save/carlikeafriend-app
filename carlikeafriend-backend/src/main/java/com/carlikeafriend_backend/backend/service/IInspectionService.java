package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.InspectionDTO;
import com.carlikeafriend_backend.backend.dto.InspectionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IInspectionService {
    InspectionResponseDTO createInspection(Long inspectorId, InspectionDTO request);
    List<InspectionResponseDTO> getInspectionsByReservation(UUID reservationId);
}

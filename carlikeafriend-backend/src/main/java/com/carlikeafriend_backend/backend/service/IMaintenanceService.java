package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.MaintenanceDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceResponseDTO;

public interface IMaintenanceService {
    void sendVehicleToMaintenance(Long vehicleId);
    MaintenanceResponseDTO registerMaintenanceCompleted(Long vehicleId, Long technicianId, MaintenanceDTO request);
}

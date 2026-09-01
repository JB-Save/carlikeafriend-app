package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.EnumOptionDTO;
import com.carlikeafriend_backend.backend.entity.ChargeType;
import com.carlikeafriend_backend.backend.entity.DocumentType;
import com.carlikeafriend_backend.backend.entity.InsuranceType;
import com.carlikeafriend_backend.backend.entity.VehicleStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carlikeafriend/metadata")
public class MetadataController {

    @GetMapping("/document-types")
    public List<EnumOptionDTO> getDocumentTypes() {
        return Arrays.stream(DocumentType.values())
                .map(type -> new EnumOptionDTO(type.name(), type.getDescription()))
                .collect(Collectors.toList());
    }

    @GetMapping("/insurance-types")
    public List<EnumOptionDTO> getInsuranceTypes() {
        return Arrays.stream(InsuranceType.values())
                .map(type -> new EnumOptionDTO(type.name(), type.getDescription()))
                .collect(Collectors.toList());
    }

    @GetMapping("/vehicle-statuses")
    public List<EnumOptionDTO> getVehicleStatus() {
        return Arrays.stream(VehicleStatus.values())
                .map(status -> new EnumOptionDTO(status.name(), status.getDescription()))
                .collect(Collectors.toList());
    }

    @GetMapping("/charge-types")
    public List<EnumOptionDTO> getChargeTypes() {
        return Arrays.stream(ChargeType.values())
                .map(type -> new EnumOptionDTO(type.name(), type.getDescription()))
                .collect(Collectors.toList());
    }
}

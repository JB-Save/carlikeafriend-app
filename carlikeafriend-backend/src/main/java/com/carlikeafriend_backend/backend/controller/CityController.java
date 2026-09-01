package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.CityBranchesResponseDTO;
import com.carlikeafriend_backend.backend.dto.CityDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.ICityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class CityController {

    private final ICityService cityService;

    @Autowired
    public CityController(ICityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/cities")
    public ResponseEntity<SimpleResponseDTO> saveCity(@RequestBody @Valid CityDTO cityDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedCity = cityService.saveCity(cityDTO);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<SimpleResponseDTO>> getAllCities() {
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }

    @GetMapping("/cities/{id}")
    public ResponseEntity<SimpleResponseDTO> getCityById(@PathVariable Long id) {
        Optional<SimpleResponseDTO> cityResponseDTO = cityService.getCityById(id);
        return cityResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cities/branches")
    public ResponseEntity<List<CityBranchesResponseDTO>> getCitiesWithBranches() {
        return new ResponseEntity<>(cityService.getCitiesWithBranches(), HttpStatus.OK);
    }

    @PutMapping("/cities/{id}")
    //@PatchMapping("/cities/{id}")
    public ResponseEntity<SimpleResponseDTO> updateCity(
            @PathVariable Long id,
            @RequestBody @Valid CityDTO cityDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO updatedCity = cityService.updateCity(id, cityDTO);
        return new ResponseEntity<>(updatedCity, HttpStatus.OK);
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

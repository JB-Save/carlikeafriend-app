package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.CityBranchesResponseDTO;
import com.carlikeafriend_backend.backend.dto.CityDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface ICityService {

    SimpleResponseDTO saveCity(CityDTO cityDTO) throws DuplicateResourceException;

    List<SimpleResponseDTO> getAllCities();

    Optional<SimpleResponseDTO> getCityById(Long id);

    List<CityBranchesResponseDTO> getCitiesWithBranches();

    SimpleResponseDTO updateCity(Long id, CityDTO cityDTO) throws DuplicateResourceException;

    void deleteCity(Long id);
}

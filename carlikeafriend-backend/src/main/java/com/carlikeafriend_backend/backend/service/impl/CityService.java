package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.BranchShortResponseDTO;
import com.carlikeafriend_backend.backend.dto.CityBranchesResponseDTO;
import com.carlikeafriend_backend.backend.dto.CityDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.City;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.repository.ICityRepository;
import com.carlikeafriend_backend.backend.service.ICityService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityService implements ICityService {

    private static final Logger logger = LoggerFactory.getLogger(CityService.class);

    private final ICityRepository cityRepository;

    @Autowired
    public CityService(ICityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO saveCity(CityDTO cityDTO) throws DuplicateResourceException {

        String cityName = StringUtils.capitalize(cityDTO.getName());

        logger.info("Intentando guardar nueva ciudad: {}", cityName);

        // Validación de duplicados por nombre
        if (cityRepository.existsByNameAndDeletedFalse(cityName)) {
            logger.warn("Ya existe una ciudad activa con el nombre: {}", cityName);
            throw new DuplicateResourceException("Ya existe una ciudad activa con el nombre: " + cityName);
        }

        // Mapear DTO a Entidad
        City city = new City();
        city.setName(cityName);

        City savedCity = cityRepository.save(city);
        logger.info("Ciudad guardada exitosamente con ID: {}", savedCity.getId());
        return mapToCityDto(savedCity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleResponseDTO> getAllCities() {
        logger.info("Buscando todas las ciudades");
        return cityRepository.findAllByDeletedFalse().stream()
                .map(this::mapToCityDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleResponseDTO> getCityById(Long id) {
        logger.info("Buscando ciudad con ID: {}", id);
        return cityRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToCityDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityBranchesResponseDTO> getCitiesWithBranches() {
        logger.info("Buscando todas las ciudades y sus sucursales");
        return cityRepository.findAllActiveWithBranches().stream()
                .map(this::mapToCityBranchesDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimpleResponseDTO updateCity(Long id, CityDTO cityDTO) throws DuplicateResourceException {

        logger.info("Intentando actualizar ciudad con ID: {}", id);

        String cityName = StringUtils.capitalize(cityDTO.getName());

        City existingCity = cityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Ciudad no encontrada con ID: " + id));

        // Validar que el nombre de la ciudad activa sea único, excluyendo la ciudad actual
        if (cityName != null && !cityName.equals(StringUtils.capitalize(existingCity.getName()))) {
            if (cityRepository.existsByNameAndIdNotAndDeletedFalse(cityName, id)) {
                throw new DuplicateResourceException("El nombre " + cityName + " ya está en uso por otra ciudad activa.");
            }
            //Actualizar datos básicos de la ciudad
            existingCity.setName(cityName);
        }

        City updatedCity = cityRepository.save(existingCity);
        return mapToCityDto(updatedCity);
    }

    @Override
    @Transactional
    public void deleteCity(Long id) {
        City city = cityRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada con ID: " + id));

        if (city.hasActiveBranches()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen sucursales activas asociadas a esta ciudad.");
        }

        // 1. LIBERAR EL NOMBRE (Renombramiento estratégico)
        String timestamp = String.valueOf(System.currentTimeMillis());
        city.setName(city.getName() + "_DELETED_" + timestamp);

        // 2. BORRADO LÓGICO
        city.setDeleted(true);

        cityRepository.save(city);
        logger.warn("Ciudad con ID {} borrada lógicamente.", id);

    }

    private SimpleResponseDTO mapToCityDto(City city) {
        return new SimpleResponseDTO(
                city.getId(),
                city.getName()
        );
    }

    private CityBranchesResponseDTO mapToCityBranchesDTO(City city) {
        List<BranchShortResponseDTO> branches = city.getBranches() != null
                ? city.getBranches().stream().map(b -> new BranchShortResponseDTO(b.getId(), b.getName(), b.getAddress())).collect(Collectors.toList())
                : new ArrayList<>();

        return new CityBranchesResponseDTO(
                city.getId(),
                city.getName(),
                branches
        );
    }
}

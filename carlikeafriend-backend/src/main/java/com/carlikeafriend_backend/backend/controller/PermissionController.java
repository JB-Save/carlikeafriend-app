package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.dto.PermissionCompleteResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IPermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class PermissionController {

    private final IPermissionService permissionService;

    @Autowired
    public PermissionController(IPermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @PostMapping("/permissions")
    public ResponseEntity<SimpleResponseDTO> savePermission(@RequestBody @Valid PermissionDTO permissionDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedPermission = permissionService.savePermission(permissionDTO);
        return new ResponseEntity<>(savedPermission, HttpStatus.CREATED);
    }


    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionCompleteResponseDTO>> findAllPermissions() {
        return new ResponseEntity<>(permissionService.getAllPermissions(), HttpStatus.OK);
    }


    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionCompleteResponseDTO> findPermissionById(@PathVariable Long id) {
        Optional<PermissionCompleteResponseDTO> permissionResponseFoundByIdDto = permissionService.getPermissionById(id);
        return permissionResponseFoundByIdDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/permissions/{id}")
    //@PatchMapping("/permissions/{id}")
    public ResponseEntity<SimpleResponseDTO> updatePermission(
            @PathVariable Long id,
            @RequestBody @Valid PermissionDTO permissionDTO)
            throws DuplicateResourceException {

        SimpleResponseDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
        return new ResponseEntity<>(updatedPermission, HttpStatus.OK);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

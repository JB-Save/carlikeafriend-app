package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseCompleteDTO;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
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
    public ResponseEntity<PermissionResponseDTO> savePermission(@RequestBody @Valid PermissionDTO permissionDTO)
            throws UniqueNameException {
        PermissionResponseDTO savedPermission = permissionService.savePermission(permissionDTO);
        return new ResponseEntity<>(savedPermission, HttpStatus.CREATED);
    }


    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseCompleteDTO>> findAllPermissions() {
        return new ResponseEntity<>(permissionService.findAllPermissions(), HttpStatus.OK);
    }


    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseCompleteDTO> findPermissionById(@PathVariable Long id) {
        Optional<PermissionResponseCompleteDTO> permissionResponseFoundByIdDto = permissionService.findPermissionById(id);
        return permissionResponseFoundByIdDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/permissions/{id}")
    //@PatchMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(
            @PathVariable Long id,
            @RequestBody @Valid PermissionDTO permissionDTO)
            throws UniqueNameException{

        PermissionResponseDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
        return new ResponseEntity<>(updatedPermission, HttpStatus.OK);
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

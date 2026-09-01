package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class RoleController {

    private final IRoleService roleService;

    @Autowired
    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<SimpleResponseDTO> saveRole(@RequestBody @Valid RoleDTO roleDTO)
            throws DuplicateResourceException {
        SimpleResponseDTO savedRole = roleService.saveRole(roleDTO);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleCompleteResponseDTO>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleCompleteResponseDTO> getRoleById(@PathVariable Long id) {
        Optional<RoleCompleteResponseDTO> roleDTO = roleService.getRoleById(id);
        return roleDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/roles/{id}")
    //@PatchMapping("/roles/{id}")
    public ResponseEntity<SimpleResponseDTO> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid RoleDTO roleDTO)
            throws DuplicateResourceException {

        SimpleResponseDTO updatedRole = roleService.updateRole(id, roleDTO);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);

    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}

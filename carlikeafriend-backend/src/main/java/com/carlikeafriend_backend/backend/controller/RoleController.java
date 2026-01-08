package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
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
    public ResponseEntity<RoleResponseDTO> saveRole(@RequestBody @Valid RoleDTO roleDTO)
            throws UniqueNameException {
        RoleResponseDTO savedRole = roleService.saveRole(roleDTO);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponseCompleteDTO>> findAllRoles() {
        return new ResponseEntity<>(roleService.findAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponseCompleteDTO> findRoleById(@PathVariable Long id) {
        Optional<RoleResponseCompleteDTO> roleDTO = roleService.findRoleById(id);
        return roleDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/roles/{id}")
    //@PatchMapping("/roles/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid RoleDTO roleDTO)
            throws UniqueNameException {

        RoleResponseDTO updatedRole = roleService.updateRole(id, roleDTO);
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);

    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}

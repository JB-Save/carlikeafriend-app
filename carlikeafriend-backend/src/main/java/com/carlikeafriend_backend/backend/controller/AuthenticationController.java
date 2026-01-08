package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/carlikeafriend/auth")
public class AuthenticationController {

    private final IUserService userService;

    @Autowired
    public AuthenticationController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) throws UniqueNameException {
        UserResponseDTO newUser = userService.registerUser(registerUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponseDTO> login(@RequestBody @Valid UserAuthenticationDTO userAuthenticationDTO) {
        UserAuthenticationResponseDTO loggedUser = userService.login(userAuthenticationDTO);
        return new ResponseEntity<>(loggedUser, HttpStatus.OK);
    }

    /*
     Endpoint PROTEGIDO para que el frontend verifique el token al recargar.
     Si el token es válido, el filtro JWT lo procesa y este método
     devuelve los datos frescos del usuario.
     Si el token es inválido, el filtro bloqueará la petición (devolverá 401)
     y el frontend sabrá que debe desloguear.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseCompleteDTO> getAuthenticatedUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // El 'Principal' es el objeto UserDetails (la entidad User)
        User currentUser = (User) authentication.getPrincipal();
        UserResponseCompleteDTO currentUserDto = new UserResponseCompleteDTO(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getRoles().stream()
                        .map(role -> new RoleResponseDTO(role.getId(), role.getName()))
                        .collect(Collectors.toList())
        );

        // Devolvemos la entidad User.
        return ResponseEntity.ok(currentUserDto);
    }
}

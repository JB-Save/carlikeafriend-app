package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterUserDTO registerUserDTO) throws DuplicateResourceException {
        UserResponseDTO newUser = userService.registerUser(registerUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponseDTO> login(@RequestBody @Valid UserAuthenticationDTO userAuthenticationDTO) {
        UserAuthenticationResponseDTO loggedUser = userService.login(userAuthenticationDTO);
        return new ResponseEntity<>(loggedUser, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO,
                                                              @AuthenticationPrincipal User currentUser) {
        // Extraer el usuario validado directamente desde el contexto de Spring Security
        // Ejecutar el servicio
        userService.changePassword(currentUser.getId(), changePasswordDTO);

        // Retornar un JSON estándar de respuesta
        return ResponseEntity.ok(Map.of("message", "Contraseña modificada correctamente."));
    }

    /*
     Endpoint PROTEGIDO para que el frontend verifique el token al recargar.
     Si el token es válido, el filtro JWT lo procesa y este método devuelve los datos frescos del usuario.
     Si el token es inválido, el filtro bloqueará la petición (devolverá 401) y el frontend sabrá que debe desloguear.
     */
    @GetMapping("/me")
    public ResponseEntity<VerifyAuthenticatedUserDTO> getAuthenticatedUserProfile(@AuthenticationPrincipal User currentUser) {
        // El 'Principal' es el objeto UserDetails (la entidad User)
        VerifyAuthenticatedUserDTO currentUserDto = new VerifyAuthenticatedUserDTO(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getRoles().stream()
                        .map(role -> new SimpleResponseDTO(role.getId(), role.getName()))
                        .collect(Collectors.toList())
        );

        // Devolvemos la entidad User.
        return ResponseEntity.ok(currentUserDto);
    }
}

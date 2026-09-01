package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend/users")
public class UserController {

    private final IUserService userService;

    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserCompleteResponseDTO>> findAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCompleteResponseDTO> getUserById(@PathVariable Long id) {
        Optional<UserCompleteResponseDTO> userDTO = userService.getUserById(id);
        return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    //@PatchMapping("/{id}")
    public ResponseEntity<UserAuthenticationResponseDTO> updateUserFromAdmin(
            @PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO) throws DuplicateResourceException {

        UserAuthenticationResponseDTO updatedUser = userService.updateUserFromAdmin(id, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UserProfileDTO profileDTO) {

        UserResponseDTO updatedProfile = userService.updateUserProfile(currentUser.getId(), profileDTO);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @GetMapping("/account")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById( @AuthenticationPrincipal User currentUser) {
        Optional<UserProfileResponseDTO> userProfileDTO = userService.getUserProfileById(currentUser.getId());
        return userProfileDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/account/me/deactivate")
    public ResponseEntity<Map<String, String>> deactivateMyAccount(@AuthenticationPrincipal User currentUser) {

        // Llamamos al método con las reglas de negocio
        userService.deleteUser(currentUser.getId());

        // Devolvemos una respuesta exitosa
        return ResponseEntity.ok(Map.of("message", "Tu cuenta ha sido desactivada y tus datos han sido procesados correctamente."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Método para manejar la solicitud de reenvío de correo.
    @PostMapping("/email/resend-confirmation")
    public ResponseEntity<String> resendConfimationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        userService.resendConfirmationEmail(email, loginUrl);

        // Siempre devuelve 200 OK con mensaje genérico, evitando la enumeración de usuarios.
        String genericMessage = "Si la cuenta está registrada, recibirá un correo electrónico de confirmación en breve. Por favor, revise su carpeta de correo no deseado.";

        return new ResponseEntity<>(genericMessage, HttpStatus.OK);
    }
}

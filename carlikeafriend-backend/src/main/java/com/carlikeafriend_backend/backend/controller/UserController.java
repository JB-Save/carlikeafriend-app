package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UserResponseCompleteDTO>> findAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseCompleteDTO> findById(@PathVariable Long id) {
        Optional<UserResponseCompleteDTO> userDTO = userService.findById(id);
        return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    //@PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserFromAdmin(
            @PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO) throws UniqueNameException {

        UserResponseDTO updatedUser = userService.updateUserFromAdmin(id, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
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

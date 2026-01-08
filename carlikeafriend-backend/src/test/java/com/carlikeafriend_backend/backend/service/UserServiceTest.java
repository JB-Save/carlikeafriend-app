package com.carlikeafriend_backend.backend.service;
import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Role;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IRoleRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IEmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IJwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setLastName("Perez");
        user.setEmail("juan@test.com");
        user.setRoles(new HashSet<>());
    }

    @Test
    @DisplayName("Buscar por Id - Debería retornar DTO si existe")
    void findById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserResponseCompleteDTO> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("juan@test.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por Id - Debería estar vacío si no existe")
    void findById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponseCompleteDTO> result = userService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Actualizar - Debería lanzar UniqueNameException si el email ya existe en otro ID")
    void updateUser_EmailConflict() {
        UserDTO dto = new UserDTO("Juan", "Perez", "existente@test.com", Set.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("existente@test.com", 1L)).thenReturn(true);

        assertThrows(UniqueNameException.class, () -> userService.updateUserFromAdmin(1L, dto));
    }

    @Test
    @DisplayName("Actualizar - Éxito")
    void updateUser_Success() {
        UserDTO dto = new UserDTO("Juan", "Modificado", "juan@test.com", Set.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUserFromAdmin(1L, dto);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Borrar - Debería lanzar excepción si el usuario no existe")
    void deleteUser_NotFound() {
       assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("Reenviar email - Debería llamar al servicio de email")
    void resendEmail_Success() {
        when(userRepository.findByEmailWithRolesAndPermissions("juan@test.com")).thenReturn(Optional.of(user));
        userService.resendConfirmationEmail("juan@test.com", "http://login.url");

        verify(emailService).sendRegistrationConfirmation(eq("juan@test.com"), any(), anyString());
    }

    @Test
    @DisplayName("Registro - Éxito")
    void registerUser_Success() throws UniqueNameException {
        RegisterUserDTO dto = new RegisterUserDTO("Juan", "Perez", "nuevo@test.com", "Password123!", new HashSet<>());
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setId(1L);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
        verify(emailService).sendRegistrationConfirmation(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Registro - Debería lanzar UniqueNameException si el email ya existe")
    void registerUser_EmailExists() {
        RegisterUserDTO dto = new RegisterUserDTO("Juan", "Perez", "juan@test.com", "Password123!", new HashSet<>());
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThrows(UniqueNameException.class, () -> userService.registerUser(dto));
    }

    @Test
    @DisplayName("Login - Éxito")
    void login_Success() {
        UserAuthenticationDTO loginDto = new UserAuthenticationDTO("juan@test.com", "Password123!");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("generated-token");

        UserAuthenticationResponseDTO result = userService.login(loginDto);

        assertNotNull(result);
        assertEquals("generated-token", result.getToken());
        assertEquals("juan@test.com", result.getUserName());
    }
}
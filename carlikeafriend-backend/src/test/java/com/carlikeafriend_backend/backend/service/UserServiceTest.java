package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.DocumentType;
import com.carlikeafriend_backend.backend.entity.Role;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.repository.IRoleRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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
        user.setDocumentType(DocumentType.CC);
        user.setDocumentNumber("1111111");
        user.setPhoneNumber("+57310333333");
        user.setNationality("Colombiano");
        user.setCountryCode("CO");
        user.setStateCode("ANT");
        user.setCity("Medellín");
        user.setAddress("Calle 29 No,32-25");
        user.setZipCode("50001");
        user.setBirthDate(LocalDate.of(2005, 1, 12));
        user.setDriverLicenseNumber("1111111");
        user.setDriverLicenseExpiry(LocalDate.of(2030, 12, 31));
        user.setEmergencyContactName("Lisa Dominguez");
        user.setEmergencyContactPhone("+57310333334");
        user.setEmail("juan@test.com");
        user.addRole(new Role());
    }

    @Test
    @DisplayName("Buscar por Id - Debería retornar DTO si existe")
    void findById_Found() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));

        Optional<UserCompleteResponseDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("juan@test.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por Id - Debería estar vacío si no existe")
    void findById_NotFound() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        Optional<UserCompleteResponseDTO> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Actualizar - Debería lanzar DuplicateResourceException si el email ya existe en otro ID")
    void updateUser_EmailConflict() {
        UserDTO dto = new UserDTO();
        dto.setEmail("existente@test.com");
        dto.setRoleIds(Set.of());
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNotAndDeletedFalse("existente@test.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateUserFromAdmin(1L, dto));
    }

    @Test
    @DisplayName("Actualizar Perfil - Éxito")
    void updateUserProfile_Success() {
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setName("Carlos");

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.updateUserProfile(1L, profileDTO);

        assertNotNull(response);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Cambiar Contraseña - Lanza excepción si contraseña actual es incorrecta")
    void changePassword_InvalidCurrentPassword() {
        ChangePasswordDTO dto = new ChangePasswordDTO("wrong", "newPass");

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("wrong"), nullable(String.class))).thenReturn(false);

        assertThrows(InvalidRangeException.class, () -> userService.changePassword(1L, dto));
    }

    @Test
    @DisplayName("Cambiar Contraseña - Éxito")
    void changePassword_Success() {
        ChangePasswordDTO dto = new ChangePasswordDTO("correct", "newPass");

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("correct"), nullable(String.class))).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        userService.changePassword(1L, dto);

        verify(userRepository).save(user);
        assertEquals("newEncoded", user.getPassword());
    }

    @Test
    @DisplayName("Borrar Usuario - Error si tiene reservas pendientes")
    void deleteUser_HasReservations_ThrowsException() {
        User mockUser = mock(User.class);
        when(mockUser.hasPendingReservations()).thenReturn(true);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(mockUser));

        assertThrows(DataIntegrityViolationException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Borrar Usuario - Éxito con limpieza de datos y borrado lógico")
    void deleteUser_Success() {
        User spyUser = spy(user);
        when(spyUser.hasPendingReservations()).thenReturn(false);
        when(spyUser.isLastAdmin(anyInt())).thenReturn(false);
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(spyUser));

        userService.deleteUser(1L);

        verify(spyUser).prepareForDeletion();
        verify(spyUser).clearAllFavorites();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    @DisplayName("Actualizar - Éxito")
    void updateUser_Success() {
        UserDTO dto = new UserDTO();
        dto.setName("Juan");
        dto.setLastName("Modificado");
        dto.setEmail("juan@test.com");
        dto.setRoleIds(Set.of());
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserAuthenticationResponseDTO result = userService.updateUserFromAdmin(1L, dto);

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
        when(userRepository.findByEmailWithRolesAndPermissionsAndDeleteFalse("juan@test.com")).thenReturn(Optional.of(user));
        userService.resendConfirmationEmail("juan@test.com", "http://login.url");

        verify(emailService).sendRegistrationConfirmation(eq("juan@test.com"), any(), anyString());
    }

    @Test
    @DisplayName("Registro - Éxito")
    void registerUser_Success() throws DuplicateResourceException {
        RegisterUserDTO dto = new RegisterUserDTO("Juan", "Perez", "nuevo@test.com", "Password123!", new HashSet<>());
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setId(1L);

        when(userRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(roleRepository.findByNameAndDeletedFalse("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
        verify(emailService).sendRegistrationConfirmation(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Registro - Debería lanzar DuplicateResourceException si el email ya existe")
    void registerUser_EmailExists() {
        RegisterUserDTO dto = new RegisterUserDTO("Juan", "Perez", "juan@test.com", "Password123!", new HashSet<>());
        when(userRepository.existsByEmailAndDeletedFalse("juan@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(dto));
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
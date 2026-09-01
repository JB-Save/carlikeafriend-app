package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    UserResponseDTO registerUser(RegisterUserDTO registerUserDTO) throws DuplicateResourceException;
    void resendConfirmationEmail(String email, String loginUrl);
    UserAuthenticationResponseDTO login(UserAuthenticationDTO userAuthenticationDTO);
    List<UserCompleteResponseDTO> getAllUsers();
    Optional<UserCompleteResponseDTO> getUserById(Long id);
    void deleteUser(Long id);
    // Método para que el Admin actualice datos del usuario
    UserAuthenticationResponseDTO updateUserFromAdmin(Long id, UserDTO userDTO) throws DuplicateResourceException;

    // Método para que el usuario actualice su propio perfil
    UserResponseDTO updateUserProfile(Long userId, UserProfileDTO profileDTO) throws DuplicateResourceException;
    Optional<UserProfileResponseDTO> getUserProfileById(Long userId);
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
}

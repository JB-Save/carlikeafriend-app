package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    UserResponseDTO registerUser(RegisterUserDTO registerUserDTO) throws UniqueNameException;
    void resendConfirmationEmail(String email, String loginUrl);
    UserAuthenticationResponseDTO login(UserAuthenticationDTO userAuthenticationDTO);
    List<UserResponseCompleteDTO> findAllUsers();
    Optional<UserResponseCompleteDTO> findById(Long id);
    UserResponseDTO updateUserFromAdmin(Long id, UserDTO userDTO) throws UniqueNameException;
    void deleteUser(Long id);
}

package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Role;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IRoleRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.IEmailService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleRepository roleRepository;
    private final IJwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IEmailService emailService;

    // Inyecta la URL de login
    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Autowired
    public UserService(IUserRepository userRepository,
                       IRoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       IJwtService jwtService,
                       AuthenticationManager authenticationManager,
                       IEmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public UserResponseDTO registerUser(RegisterUserDTO registerUserDTO) throws UniqueNameException {

        logger.info("Intentando registrar nuevo usuario con email: {}", registerUserDTO.getEmail());

        //Validación de duplicado por email
        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            logger.warn("Registro fallido: el email ya existe: {}", registerUserDTO.getEmail());
            throw new UniqueNameException("El email ya existe.");
        }

        //Mapear DTO a Entidad
        User newUser = new User();
        newUser.setName(registerUserDTO.getName());
        newUser.setLastName(registerUserDTO.getLastName());
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> {
                    logger.error("Error crítico: Rol por defecto (USER) no encontrado.");
                    return new ResourceNotFoundException("Error interno: Rol por defecto no encontrado.");
                });
        //defaultRole.addUser(newUser);
        newUser.setRoles(Collections.singleton(defaultRole));

        User savedUser = userRepository.save(newUser);

        // Envío de correo inicial
        emailService.sendRegistrationConfirmation(savedUser.getEmail(), savedUser.getName() + " " + savedUser.getLastName(), loginUrl);
        logger.info("Correo de confirmación encolado para: {}", savedUser.getEmail());

        return convertUserToDto(savedUser);
    }

    @Override
    public void resendConfirmationEmail(String email, String loginUrl) {

        Optional<User> userOptional = userRepository.findByEmailWithRolesAndPermissions(email);

        // Llama al servicio de email (que es asíncrono)
        userOptional.ifPresent(user -> emailService.sendRegistrationConfirmation(user.getEmail(), user.getName() + " " + user.getLastName(), loginUrl));
        // Si el usuario no existe, la función simplemente retorna sin enviar nada,
        // manteniendo la seguridad contra la enumeración de usuarios.
    }

    @Override
    @Transactional(readOnly = true)
    public UserAuthenticationResponseDTO login(UserAuthenticationDTO userAuthenticationDTO) {
        logger.info("Intentando iniciar sesión para el email: {}", userAuthenticationDTO.getEmail());
        // Autenticar. Esto usa nuestro UserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userAuthenticationDTO.getEmail(),
                        userAuthenticationDTO.getPassword()
                )
        );

        // Obtener el Principal. Este 'user' YA tiene los roles y permisos cargados
        User user = (User) authentication.getPrincipal();

        // Generar token
        String jwt = jwtService.generateToken(user);
        logger.info("Inicio de sesión exitoso para el usuario con ID: {}", user.getId());


        // Construir DTO de respuesta
        // Esto ahora es seguro y no lanzará LazyInitializationException
        List<RoleResponseDTO> rolesDto = user.getRoles().stream()
                .map(role -> new RoleResponseDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());

        return new UserAuthenticationResponseDTO(
                jwt,
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                rolesDto
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseCompleteDTO> findAllUsers() {
        logger.info("Buscando todos los usuarios.");
        return userRepository.findAll().stream()
                .map(this::convertUserFoundByIdToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseCompleteDTO> findById(Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        return userRepository.findById(id)
                .map(this::convertUserFoundByIdToDto);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserFromAdmin(Long id, UserDTO userDTO) throws UniqueNameException {
        logger.info("Intentando actualizar usuario con ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Validar que el email del usuario sea único, excluyendo el actual
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail()) && userRepository.existsByEmailAndIdNot(userDTO.getEmail(), id)) {
           throw new UniqueNameException("El nuevo email ya está registrado.");
        }

        // Actualizar datos básicos del usuario
        Optional.ofNullable(userDTO.getName()).ifPresent(existingUser::setName);
        Optional.ofNullable(userDTO.getLastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(userDTO.getEmail()).ifPresent(existingUser::setEmail);
            /*// Si se proporciona una nueva contraseña, codifícarla antes de guardarla
            if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            */

        // Actualizar relaciones (Lógica simplificada usando métodos auxiliares)
        updateRoles(existingUser, userDTO.getRoles());

        User updatedUser = userRepository.save(existingUser);
        return convertUserToDto(updatedUser);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        userRepository.delete(user);
        logger.warn("Usuario eliminado con ID: {}", id);
    }

    // Métodos Auxiliares para limpiar el código principal

    private void updateRoles(User user, Set<Long> newIds) {
        if (newIds == null) return;
        Set<Long> finalIds = newIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        // Eliminar las que no están
        user.getRoles().removeIf(c -> !finalIds.contains(c.getId()));

        // Agregar las nuevas
        for (Long id : finalIds) {
            if (user.getRoles().stream().noneMatch(c -> c.getId().equals(id))) {
                user.getRoles().add(roleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id)));
            }
        }
    }

    private UserResponseDTO convertUserToDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    private UserResponseCompleteDTO convertUserFoundByIdToDto(User user) {
        List<RoleResponseDTO> roleResponseDtos = user.getRoles().stream()
                .map(role -> new RoleResponseDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());

        return new UserResponseCompleteDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                roleResponseDtos
        );
    }
}

package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.DocumentType;
import com.carlikeafriend_backend.backend.entity.Role;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.repository.IRoleRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.IEmailService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IUserService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
    public UserResponseDTO registerUser(RegisterUserDTO registerUserDTO) throws DuplicateResourceException {

        String email = StringUtils.normalizeToLowerCase(registerUserDTO.getEmail());

        logger.info("Intentando registrar nuevo usuario con email: {}", email);

        //Validación de duplicado por email
        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            logger.warn("Registro fallido: Ya existe un usuario activo con el email: {}", email);
            throw new DuplicateResourceException("Ya existe un usuario activo");
        }

        //Mapear DTO a Entidad
        User newUser = new User();
        newUser.setName(StringUtils.capitalize(registerUserDTO.getName()));
        newUser.setLastName(StringUtils.capitalize(registerUserDTO.getLastName()));
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        // Asignar rol por defecto usando el método auxiliar
        Role defaultRole = roleRepository.findByNameAndDeletedFalse("USER")
                .orElseThrow(() -> {
                    logger.error("Error crítico: Rol por defecto (USER) no encontrado.");
                    return new ResourceNotFoundException("Error interno: Rol por defecto no encontrado.");
                });
        //defaultRole.addUser(newUser);
        //newUser.setRoles(Collections.singleton(defaultRole));

        // Creamos un set temporal para usar el método auxiliar
        updateRoles(newUser, Collections.singleton(defaultRole.getId()));

        User savedUser = userRepository.save(newUser);

        // Envío de correo inicial
        emailService.sendRegistrationConfirmation(savedUser.getEmail(), savedUser.getName() + " " + savedUser.getLastName(), loginUrl);
        logger.info("Correo de confirmación encolado para: {}", savedUser.getEmail());

        return mapToUserDto(savedUser);
    }

    @Override
    public void resendConfirmationEmail(String email, String loginUrl) {
        // Llama al servicio de email (que es asíncrono)
        // Si el usuario no existe, la función simplemente retorna sin enviar nada,
        // manteniendo la seguridad contra la enumeración de usuarios.
        userRepository.findByEmailWithRolesAndPermissionsAndDeleteFalse(email).ifPresent(user -> emailService.sendRegistrationConfirmation(user.getEmail(), user.getName() + " " + user.getLastName(), loginUrl));
    }

    @Override
    @Transactional(readOnly = true)
    public UserAuthenticationResponseDTO login(UserAuthenticationDTO userAuthenticationDTO) {
        logger.info("Intentando iniciar sesión para el email: {}", userAuthenticationDTO.getEmail());
        // Autenticar. Esto usa nuestro UserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        StringUtils.normalizeToLowerCase(userAuthenticationDTO.getEmail()),
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
        List<SimpleResponseDTO> rolesDto = user.getRoles().stream()
                .map(role -> new SimpleResponseDTO(role.getId(), role.getName()))
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
    public List<UserCompleteResponseDTO> getAllUsers() {
        logger.info("Buscando todos los usuarios.");
        return userRepository.findAllByDeletedFalse().stream()
                .map(this::mapToUserCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserCompleteResponseDTO> getUserById(Long id) {
        logger.info("Buscando usuario con ID: {}", id);
        return userRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToUserCompleteDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileResponseDTO> getUserProfileById(Long userId) {
        logger.info("Buscando perfil de usuario con ID: {}", userId);
        return userRepository.findByIdAndDeletedFalse(userId)
                .map(this::mapToUserProfileDto);
    }

    @Override
    @Transactional
    public UserAuthenticationResponseDTO updateUserFromAdmin(Long id, UserDTO userDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar usuario con ID: {}", id);

        String email = StringUtils.normalizeToLowerCase(userDTO.getEmail());

        User existingUser = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Usuario no encontrado con ID: " + id));

        // Validar que el email del usuario activo sea único, excluyendo el actual
        if (email != null && !email.equals(StringUtils.normalizeToLowerCase(existingUser.getEmail()))) {
            if (userRepository.existsByEmailAndIdNotAndDeletedFalse(email, id)) {
                throw new DuplicateResourceException("Ya existe otro usuario con el email provisto.");
            }
            existingUser.setEmail(email);
        }

        // Actualizar datos básicos del usuario
        Optional.ofNullable(userDTO.getName()).map(StringUtils::capitalize).ifPresent(existingUser::setName);
        Optional.ofNullable(userDTO.getLastName()).map(StringUtils::capitalize).ifPresent(existingUser::setLastName);
        Optional.ofNullable(userDTO.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);
        Optional.ofNullable(userDTO.getDocumentType()).ifPresent(dt -> existingUser.setDocumentType(DocumentType.validate(dt)));
        Optional.ofNullable(userDTO.getDocumentNumber()).ifPresent(existingUser::setDocumentNumber);
        Optional.ofNullable(userDTO.getAddress()).ifPresent(existingUser::setAddress);
        Optional.ofNullable(userDTO.getCity()).map(StringUtils::capitalize).ifPresent(existingUser::setCity);
        Optional.ofNullable(userDTO.getCountryCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setCountryCode);
        Optional.ofNullable(userDTO.getStateCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setStateCode);
        Optional.ofNullable(userDTO.getZipCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setZipCode);
        Optional.ofNullable(userDTO.getNationality()).map(StringUtils::capitalize).ifPresent(existingUser::setNationality);
        Optional.ofNullable(userDTO.getBirthDate()).ifPresent(existingUser::setBirthDate);
        Optional.ofNullable(userDTO.getDriverLicenseNumber()).ifPresent(existingUser::setDriverLicenseNumber);
        Optional.ofNullable(userDTO.getDriverLicenseExpiry()).ifPresent(existingUser::setDriverLicenseExpiry);
        Optional.ofNullable(userDTO.getEmergencyContactName()).ifPresent(existingUser::setEmergencyContactName);
        Optional.ofNullable(userDTO.getEmergencyContactPhone()).ifPresent(existingUser::setEmergencyContactPhone);

        // Usar método auxiliar
        if (userDTO.getRoleIds() != null) {
            // Obtenemos la cantidad de admins activos
            int totalAdmins = userRepository.findAllAdmins().size();
            //obtenemos el rol ADMIN si exite en nuestros roles
            Role adminRole = existingUser.getRoles().stream().filter(role -> "ADMIN".equals(role.getName())).findFirst().orElse(null);
            if (adminRole != null) {
                //Verificamos si el rol ADMIN es excluido en la nueva lista y si somos el unico Administrador
                boolean adminRoleIdIsExcluded = !userDTO.getRoleIds().contains(adminRole.getId());
                if (existingUser.isLastAdmin(totalAdmins) && adminRoleIdIsExcluded) {
                    throw new DataIntegrityViolationException("Actualización denegada: No se puede cambiar o eliminar el rol ADMIN al único administrador del sistema.");
                }
            }

            updateRoles(existingUser, userDTO.getRoleIds());
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("El Administrador actualizó al usuario ID: {}", id);

        // Generar el nuevo token con los roles actualizados.
        String newToken = jwtService.generateToken(updatedUser);

        return mapToUserUpdatedResponseDto(updatedUser, newToken);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserProfile(Long userId, UserProfileDTO profileDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar perfil del usuario con ID: {}", userId);

        User existingUser = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Usuario no encontrado."));

        // Solo actualizamos campos permitidos (Demográficos y Contacto)
        Optional.ofNullable(profileDTO.getName()).map(StringUtils::capitalize).ifPresent(existingUser::setName);
        Optional.ofNullable(profileDTO.getLastName()).map(StringUtils::capitalize).ifPresent(existingUser::setLastName);
        Optional.ofNullable(profileDTO.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);
        Optional.ofNullable(profileDTO.getDocumentType()).ifPresent(dt -> existingUser.setDocumentType(DocumentType.validate(dt)));
        Optional.ofNullable(profileDTO.getDocumentNumber()).ifPresent(existingUser::setDocumentNumber);
        Optional.ofNullable(profileDTO.getAddress()).ifPresent(existingUser::setAddress);
        Optional.ofNullable(profileDTO.getCity()).map(StringUtils::capitalize).ifPresent(existingUser::setCity);
        Optional.ofNullable(profileDTO.getCountryCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setCountryCode);
        Optional.ofNullable(profileDTO.getStateCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setStateCode);
        Optional.ofNullable(profileDTO.getZipCode()).map(StringUtils::normalizeToUpperCase).ifPresent(existingUser::setZipCode);
        Optional.ofNullable(profileDTO.getNationality()).map(StringUtils::capitalize).ifPresent(existingUser::setNationality);
        Optional.ofNullable(profileDTO.getBirthDate()).ifPresent(existingUser::setBirthDate);
        Optional.ofNullable(profileDTO.getDriverLicenseNumber()).ifPresent(existingUser::setDriverLicenseNumber);
        Optional.ofNullable(profileDTO.getDriverLicenseExpiry()).ifPresent(existingUser::setDriverLicenseExpiry);
        Optional.ofNullable(profileDTO.getEmergencyContactName()).map(StringUtils::capitalize).ifPresent(existingUser::setEmergencyContactName);
        Optional.ofNullable(profileDTO.getEmergencyContactPhone()).ifPresent(existingUser::setEmergencyContactPhone);
        Optional.ofNullable(profileDTO.getEmail()).ifPresent(existingUser::setEmail);

        User updatedUser = userRepository.save(existingUser);
        logger.info("Perfil actualizado exitosamente para el usuario ID: {}", userId);

        return mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        logger.info("Intentando actualizar la contraseña para el usuario ID: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        // Verificar criptográficamente que la contraseña actual ingresada coincida con la almacenada
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            logger.warn("Fallo al cambiar la contraseña: La contraseña actual es incorrecta para el usuario ID: {}", userId);
            throw new InvalidRangeException("La contraseña actual es incorrecta.");
        }

        // Hashear la nueva contraseña y actualizar
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        logger.info("Contraseña actualizada exitosamente para el usuario ID: {}", userId);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Obtenemos la cantidad de admins activos
        int totalAdmins = userRepository.findAllAdmins().size();

        // Validar las reglas de negocio
        if (user.hasPendingReservations()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen reservas pendientes asociados a este usuario.");
        }

        if (user.isLastAdmin(totalAdmins)) {
            throw new DataIntegrityViolationException("Operación denegada: No se puede eliminar al único administrador del sistema.");
        }

        // Preparar los datos de la entidad (Anonimiza si es USER, apaga isEnabled para todos)
        user.prepareForDeletion();

        // Limpiar relaciones (Eliminación física de la tabla intermedia de favoritos)
        user.clearAllFavorites();

        // Borrado Lógico
        user.setDeleted(true); // Campo de la clase padre Auditable

        userRepository.save(user);
        logger.warn("Usuario con ID: {} borrado lógicamente.", id);

    }

    // MÉTODOS AUXILIARES
    private void updateRoles(User user, Set<Long> newIds) {
        // 1. Limpieza Bidireccional
        if (user.getRoles() != null) {
            // Iterar sobre copia
            new ArrayList<>(user.getRoles()).forEach(role -> {
                role.removeUser(user); // Sincroniza lado Role
            });
        }

        // 2. Asignación usando métodos de conveniencia
        if (newIds != null && !newIds.isEmpty()) {
            newIds.stream().filter(Objects::nonNull).forEach(id -> {
                Role role = roleRepository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
                role.addUser(user); // Sincroniza ambos lados
            });
        }
    }

    private UserResponseDTO mapToUserDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    private UserCompleteResponseDTO mapToUserCompleteDto(User user) {
        List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        return new UserCompleteResponseDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getDocumentType().toString() ,
                user.getDocumentNumber(),
                user.getPhoneNumber(),
                user.getNationality(),
                user.getCountryCode(),
                user.getStateCode(),
                user.getCity(),
                user.getAddress(),
                user.getZipCode(),
                user.getBirthDate(),
                user.getDriverLicenseNumber(),
                user.getDriverLicenseExpiry(),
                user.getEmergencyContactName(),
                user.getEmergencyContactPhone(),
                user.getEmail(),
                roleIds
        );
    }

    private UserProfileResponseDTO mapToUserProfileDto(User user) {

        return new UserProfileResponseDTO(
                user.getName(),
                user.getLastName(),
                user.getDocumentType().toString(),
                user.getDocumentNumber(),
                user.getPhoneNumber(),
                user.getNationality(),
                user.getCountryCode(),
                user.getStateCode(),
                user.getCity(),
                user.getAddress(),
                user.getZipCode(),
                user.getBirthDate(),
                user.getDriverLicenseNumber(),
                user.getDriverLicenseExpiry(),
                user.getEmergencyContactName(),
                user.getEmergencyContactPhone(),
                user.getEmail()
        );
    }

    private UserAuthenticationResponseDTO mapToUserUpdatedResponseDto(User user, String token) {
        // Extraemos los roles
        List<SimpleResponseDTO> rolesDto = user.getRoles().stream()
                .map(role -> new SimpleResponseDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());

        return new UserAuthenticationResponseDTO(
                token,
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                rolesDto
        );
    }
}

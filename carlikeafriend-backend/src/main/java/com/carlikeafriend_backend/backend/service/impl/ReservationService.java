package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.entity.ChargeType;
import com.carlikeafriend_backend.backend.exception.*;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IFinancialConfigurationService;
import com.carlikeafriend_backend.backend.service.IReservationService;
import com.carlikeafriend_backend.backend.util.DateValidationUtils;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService implements IReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final IReservationRepository reservationRepository;
    private final IUserRepository userRepository;
    private final IVehicleRepository vehicleRepository;
    private final IBranchRepository branchRepository;
    private final IBranchTransferFeeRepository transferFeeRepository;
    private final IBranchAddonRepository branchAddonRepository;
    private final IReservationExtraRepository reservationExtraRepository;
    private final IFinancialConfigurationService financialConfigService;
    private final IReviewRepository reviewRepository;

    @Autowired
    public ReservationService(IReservationRepository reservationRepository,
                              IUserRepository userRepository,
                              IVehicleRepository vehicleRepository,
                              IBranchRepository branchRepository,
                              IBranchTransferFeeRepository transferFeeRepository,
                              IBranchAddonRepository branchAddonRepository,
                              IReservationExtraRepository reservationExtraRepository,
                              IFinancialConfigurationService financialConfigService,
                              IReviewRepository reviewRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
        this.transferFeeRepository = transferFeeRepository;
        this.branchAddonRepository = branchAddonRepository;
        this.reservationExtraRepository = reservationExtraRepository;
        this.financialConfigService = financialConfigService;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public ReservationResponseDTO createReservation(Long userId, ReservationDTO request) {
        logger.info("Intentando crear nueva reserva al producto con ID: {}", request.getProductId());

        // Traemos la configuración financiera
        FinancialConfigurationResponseDTO config = financialConfigService.getConfiguration();

        // 1. Uso de la utilidad centralizada para validar las fechas de la reserva
        DateValidationUtils.validateBookingDates(request.getPickupDatetime(), request.getReturnDatetime(), config, "En Reserva");

        // 2. Obtener la lista de IDs de vehículos disponibles
        List<Long> availableVehicleIds = vehicleRepository.findAvailableVehicleIdsForProduct(
                request.getProductId(), request.getPickupBranchId(),
                request.getPickupDatetime(), request.getReturnDatetime()
        );

        if (availableVehicleIds.isEmpty()) {
            throw new ResourceNotAvailableException("No hay vehículos disponibles para este producto en las fechas y sucursal seleccionadas.");
        }

        Vehicle assignedVehicle = null;

        // 3. Intentar bloquear pesimistamente el primer vehículo disponible
        for (Long vehicleId : availableVehicleIds) {
            try {
                Optional<Vehicle> lockedVehicleOpt = vehicleRepository.findByIdWithLock(vehicleId);
                if (lockedVehicleOpt.isPresent()) {
                    // Doble chequeo una vez adquirido el candado (por si otro hilo lo reservó hace milisegundos)
                    boolean isStillBooked = reservationRepository.isVehicleBooked(
                            vehicleId, request.getPickupDatetime(), request.getReturnDatetime()
                    );
                    if (!isStillBooked) {
                        assignedVehicle = lockedVehicleOpt.get();
                        break; // ¡Vehículo asegurado!
                    }
                }
            } catch (Exception e) {
                logger.warn("El vehículo {} fue bloqueado por otra transacción, intentando con el siguiente...", vehicleId);
                // Continuar con el siguiente ID en la lista
            }
        }

        if (assignedVehicle == null) {
            throw new ResourceNotAvailableException("Alta concurrencia: Los vehículos disponibles acaban de ser reservados por otros usuarios.");
        }

        // 4. Obtener Entidades
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Branch pickupBranch = branchRepository.findByIdAndDeletedFalse(request.getPickupBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de recogida no encontrada"));
        Branch returnBranch = branchRepository.findByIdAndDeletedFalse(request.getReturnBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de entrega no encontrada"));

        // 5. Validar perfil completo
        if (!user.isProfileComplete()) {
            throw new BookingStateConflictException("Tu perfil está incompleto. Por favor, actualiza tus datos personales y de licencia antes de reservar.");
        }

        //6. Validar tipo de seguro
        InsuranceType insuranceType = InsuranceType.validate(request.getInsuranceType());

        // 7. Construir Reserva y Snapshots
        Reservation reservation = new Reservation();

        // 8. PROCESAR EXTRAS
        if (request.getExtras() != null && !request.getExtras().isEmpty()) {
            for (AddonRequestDTO extraDto : request.getExtras()) {

                // A. Bloqueo Pesimista: Aseguramos el registro de inventario de esta sucursal
                BranchAddon branchStock = branchAddonRepository.findByBranchIdAndAddonIdWithLock(request.getPickupBranchId(), extraDto.getAddonId())
                        .orElseThrow(() -> new ResourceNotFoundException("El extra seleccionado no existe en la sucursal de origen."));

                Addon addon = branchStock.getAddon();

                // B. Validación 1: Límite de Negocio (Tope máximo por reserva)
                if (extraDto.getQuantity() > addon.getMaxQuantityPerReservation()) {
                    throw new InvalidRangeException("No puedes exceder el límite de " + addon.getMaxQuantityPerReservation() + " unidades para el extra: " + addon.getName());
                }

                // C. ¿Cuántos están ocupados en esas fechas?
                Integer reservedSum = reservationExtraRepository.sumQuantityReservedInDateRange(
                        request.getPickupBranchId(),
                        extraDto.getAddonId(),
                        request.getPickupDatetime(),
                        request.getReturnDatetime()
                );

                // Manejo de nulos preventivo por si la DB devuelve NULL en la sumatoria
                int currentlyReserved = (reservedSum != null) ? reservedSum : 0;

                // Cálculo dinámico
                int availableRightNow = branchStock.getTotalStock() - currentlyReserved;

                if (availableRightNow < extraDto.getQuantity()) {
                    throw new ResourceNotAvailableException("Stock insuficiente. Solo quedan " + availableRightNow + " unidades de " + addon.getName() + " para esas fechas.");
                }

                // D. Si hay, lo agregamos a la reserva
                ReservationExtra resExtra = new ReservationExtra();
                resExtra.setAddon(addon);
                resExtra.setQuantity(extraDto.getQuantity());
                resExtra.setUnitPriceSnapshot(addon.getCurrentPrice()); // ¡Congelamos el precio!

                reservation.addExtra(resExtra); // Usamos método de conveniencia
            }
        }

        // 9. CREACIÓN DE LA PRE-RESERVA (HOLD)
        reservation.setReservationStatus(ReservationStatus.PENDING_CONFIRMATION);
        reservation.setPickupDatetime(request.getPickupDatetime());
        reservation.setReturnDatetime(request.getReturnDatetime());

        // --- LÓGICA DE CONDUCTOR PRINCIPAL ---
        // Seteamos los Snapshots del Cliente
        if (Boolean.TRUE.equals(request.isUserTheMainDriver())) {
            // 1. Validar edad >= 18
            DateValidationUtils.validateDriverAge(user.getBirthDate(), request.getPickupDatetime(), 18);
            // 2. Licencia de conducir vigente
            DateValidationUtils.validateLicenseExpiration(user.getDriverLicenseExpiry(), request.getReturnDatetime());
            // 3. El usuario autenticado es el conductor
            reservation.setRenterFullNameSnapshot(user.getName() + " " + user.getLastName());
            reservation.setRenterEmailSnapshot(user.getEmail());
            reservation.setRenterPhoneSnapshot(user.getPhoneNumber());
            reservation.setRenterIdNumberTypeSnapshot(user.getDocumentType().name());
            reservation.setRenterIdNumberSnapshot(user.getDocumentNumber());
            reservation.setRenterDriverLicenseSnapshot(user.getDriverLicenseNumber());
            reservation.setRenterDriverLicenseExpirySnapshot(user.getDriverLicenseExpiry());
            reservation.setRenterNationalitySnapshot(user.getNationality());
            reservation.setRenterCountrySnapshot(user.getCountryCode());
            reservation.setRenterStateOrDepartmentSnapshot(user.getStateCode());
            reservation.setRenterCitySnapshot(user.getCity());
            reservation.setRenterZipCodeSnapshot(user.getZipCode());
            reservation.setRenterAddressSnapshot(user.getAddress());
            reservation.setRenterBirthDateSnapshot(user.getBirthDate());
            reservation.setRenterEmergencyContactNameSnapshot(user.getEmergencyContactName());
            reservation.setRenterEmergencyContactPhoneSnapshot(user.getEmergencyContactPhone());

        } else {
            // 1. Un tercero es el conductor (Ej. reserva corporativa o familiar)
            if (request.getDriverDetails() == null) {
                throw new InvalidResourceStateException("Debe proporcionar los datos del conductor si el usuario no es el conductor principal.");
            }
            // 2. Validar edad >= 18
            DateValidationUtils.validateDriverAge(request.getDriverDetails().getBirthDate(), request.getPickupDatetime(), 18);
            // 3. Licencia de conducir vigente
            DateValidationUtils.validateLicenseExpiration(request.getDriverDetails().getDriverLicenseExpiry(), request.getReturnDatetime());

            reservation.setRenterFullNameSnapshot(StringUtils.capitalize(request.getDriverDetails().getFullName()));
            reservation.setRenterEmailSnapshot(user.getEmail()); // El email sigue siendo del User (dueño de la cuenta)
            reservation.setRenterPhoneSnapshot(request.getDriverDetails().getPhoneNumber());
            reservation.setRenterIdNumberTypeSnapshot(DocumentType.validate(request.getDriverDetails().getDocumentType()).name());
            reservation.setRenterIdNumberSnapshot(request.getDriverDetails().getDocumentNumber());
            reservation.setRenterDriverLicenseSnapshot(request.getDriverDetails().getDriverLicenseNumber());
            reservation.setRenterDriverLicenseExpirySnapshot(request.getDriverDetails().getDriverLicenseExpiry());
            reservation.setRenterNationalitySnapshot(StringUtils.capitalize(request.getDriverDetails().getNationality()));
            reservation.setRenterCountrySnapshot(user.getCountryCode());
            reservation.setRenterStateOrDepartmentSnapshot(user.getStateCode());
            reservation.setRenterCitySnapshot(user.getCity());
            reservation.setRenterZipCodeSnapshot(user.getZipCode());
            reservation.setRenterAddressSnapshot(user.getAddress()); // Usamos dirección de facturación del User
            reservation.setRenterBirthDateSnapshot(request.getDriverDetails().getBirthDate());
            reservation.setRenterEmergencyContactNameSnapshot(StringUtils.capitalize(request.getDriverDetails().getEmergencyContactName()));
            reservation.setRenterEmergencyContactPhoneSnapshot(request.getDriverDetails().getEmergencyContactPhone());
        }

        reservation.setUser(user);
        reservation.setVehicle(assignedVehicle);
        reservation.setPickupBranch(pickupBranch);
        reservation.setReturnBranch(returnBranch);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setInsuranceType(insuranceType);

        reservation.setVehicleLicensePlateSnapshot(assignedVehicle.getLicensePlate());
        reservation.setPickupBranchNameSnapshot(pickupBranch.getName());
        reservation.setReturnBranchNameSnapshot(returnBranch.getName());
        reservation.setProductNameSnapshot(assignedVehicle.getProduct() != null ? assignedVehicle.getProduct().getName() : "N/A");

        // SNAPSHOT DE POLÍTICAS VIGENTES
        // Buscamos las políticas asociadas al producto (vehículo) en este momento exacto
        String currentPolicies = assignedVehicle.getProduct().getPolicies().stream()
                .map(policy -> policy.getName() + ": " + policy.getContent())
                .reduce((p1, p2) -> p1 + "\n\n" + p2)
                .orElse("Términos y condiciones estándar aplicados.");

        reservation.setPoliciesSnapshot(currentPolicies);
        reservation.setPolicyHash(generateSHA256(currentPolicies));

        // Los niveles de combustible y penalidades nacen nulos/cero
        reservation.setFuelLevelAtPickupSnapshot(null);
        reservation.setFuelLevelAtReturnSnapshot(null);
        reservation.setCancellationPolicyAppliedSnapshot(0.0);

        // Logística y Conductor
        reservation.setArrivalFlightNumber(StringUtils.normalizeToUpperCase(request.getArrivalFlightNumber()));
        reservation.setUserTheMainDriver(request.isUserTheMainDriver());

        // Estado de pago inicial
        reservation.setPaymentStatus(PaymentStatus.PENDING);

        // 10. CÁLCULO DE PRECIOS (La Lógica Matemática)
        calculatePricing(reservation, assignedVehicle, config);

        // Definir el tiempo de expiración (ej. 10 minutos desde AHORA)
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);
        reservation.setExpirationDate(expiration);

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Reserva guardada exitosamente con ID: {}", savedReservation.getId());
        return mapToReservationDTO(savedReservation);
    }

    @Override
    @Transactional
    public void startRental(UUID id, Long employeeId) {
        logger.info("Intentando iniciar la reserva con ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        User user = userRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 1. Ventana de tiempo (Solo permitir inicio 2 horas antes de lo pactado)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime allowedStartTime = reservation.getPickupDatetime().minusHours(2);
        if (now.isBefore(allowedStartTime)) {
            throw new ResourceNotAvailableException("Es muy pronto para iniciar este alquiler. El vehículo estará disponible 2 horas antes de la hora programada.");
        }

        //2. Validar estado Confirmed
        if (!reservation.isConfirmed()) {
            throw new BookingStateConflictException("Solo las reservas confirmadas pueden ser iniciadas.");
        }

        //3. Validar inspección pickup
        if (!reservation.hasPickupInspection()) {
            throw new BookingStateConflictException("No se puede iniciar el alquiler sin una inspección de entrega (Pickup) registrada.");
        }
        // 4. Delegar a la entidad el cambio de estado de la reserva a In_Progress
        reservation.startRental();

        // 5. Cambiar estado del vehículo
        Vehicle vehicle = reservation.getVehicle();
        if (vehicle.getVehicleStatus() != VehicleStatus.AVAILABLE) {
            throw new BookingStateConflictException("El vehículo no está disponible para ser alquilado'. Estado actual: " + vehicle.getVehicleStatus());
        }
        vehicle.rentOut();

        reservationRepository.save(reservation);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void completeRental(UUID id, Long employeeId) {

        logger.info("Intentando completar reserva con ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        User user = userRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));


        //1. Validar estado In_Progress
        if (!reservation.isInProgress()) {
            throw new BookingStateConflictException("Solo las reservas en progreso pueden marcarse como completadas.");
        }

        //2. Validar inspección return
        if (!reservation.hasReturnInspection()) {
            throw new BookingStateConflictException("No se puede completar el alquiler sin una inspección de devolución (Return) registrada.");
        }

        // 3. Delegar a la entidad el cambio de estado a Completed
        reservation.completeRental();

        Vehicle vehicle = reservation.getVehicle();

        // 4. Extraer la inspección de retorno para tomar datos reales
        Inspection returnInspection = reservation.getInspections().stream()
                .filter(i -> i.getInspectionType().name().equalsIgnoreCase("Return"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Inspección de retorno no encontrada en memoria."));

        // 5. Actualizar el kilometraje real del vehículo
        if (returnInspection.getMileage() != null && returnInspection.getMileage() > vehicle.getCurrentMileage()) {
            vehicle.setCurrentMileage(returnInspection.getMileage());
        }

        // 6. Reubicar el vehículo en la sucursal de entrega
        Branch returnBranch = reservation.getReturnBranch();
        vehicle.setCurrentBranch(returnBranch);

        // 7. Liberar el vehículo
        vehicle.release();

        reservationRepository.save(reservation);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationResponseDTO> getReservationById(UUID id) {
        logger.info("Buscando reserva con ID: {}", id);
        return reservationRepository.findById(id)
                .map(this::mapToReservationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseBlockedDatesDTO getBlockedDatesForProduct(Long productId, Long branchId, LocalDate startDate, LocalDate endDate) {
        logger.info("Calculando fechas bloqueadas para el producto ID: {} en la sucursal ID: {} entre {} y {}", productId, branchId, startDate, endDate);

        LocalDate today = LocalDate.now();

        // Validar que la fecha de inicio no sea en el pasado
        if (startDate.isBefore(today)) {
            logger.warn("La fecha de inicio: {}, no puede ser anterior a hoy: {}.", startDate, today);
            throw new InvalidRangeException("La fecha de inicio no puede ser anterior a hoy.");
        }

        // Validar lógicamente que startDate no sea posterior a endDate
        if (startDate.isAfter(endDate)) {
            logger.warn("La fecha de inicio: {}, no puede ser posterior a la fecha final: {}.", startDate, endDate);
            throw new InvalidRangeException("La fecha de inicio no puede ser posterior a la fecha de final.");
        }

        // 1. Obtener el total de vehículos físicos disponibles en una sucursal específica (V_total_sucursal)
        int totalVehicles = vehicleRepository.countAvailableVehiclesByProductIdAndBranchId(productId, branchId);
        // Si no hay inventario físico en la sucursal específica, todas las fechas están bloqueadas
        if (totalVehicles == 0) {
            List<LocalDate> allDates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
            return new ResponseBlockedDatesDTO(allDates);
        }

        // 2. Traer las reservas que intersectan con el rango en la sucursal (R_d_sucursal)
        // Convertimos LocalDate a LocalDateTime para abarcar todo el día
        List<Reservation> reservations = reservationRepository.findActiveReservationsForProductAndBranchInDateRange(
                productId,
                branchId,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 3. Evaluación matemática día por día
        List<LocalDate> blockedDates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDate finalCurrentDate = currentDate; // Necesario para el lambda

            // Sumatoria de reservas para el día actual (\sum R_d)
            long activeReservationsForDay = reservations.stream()
                    .filter(r -> {
                        LocalDate resPickup = r.getPickupDatetime().toLocalDate();
                        LocalDate resReturn = r.getReturnDatetime().toLocalDate();
                        // Una reserva aplica a este día si el día actual está entre pickup y return (inclusivo)
                        return !resPickup.isAfter(finalCurrentDate) && !resReturn.isBefore(finalCurrentDate);
                    })
                    .count();

            // Si las reservas igualan o superan el inventario físico en la sucursal, la fecha se bloquea
            if (activeReservationsForDay >= totalVehicles) {
                blockedDates.add(currentDate);
            }

            currentDate = currentDate.plusDays(1);
        }

        return new ResponseBlockedDatesDTO(blockedDates);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReservationResponseDTO> getUserReservations(Long userId, String type) {
        List<ReservationStatus> statuses;
        Sort sort;

        if ("upcoming".equalsIgnoreCase(type)) {
            statuses = List.of(
                    ReservationStatus.CONFIRMED,
                    ReservationStatus.IN_PROGRESS
            );
            sort = Sort.by("pickupDatetime").ascending();
        } else {
            statuses = List.of(
                    ReservationStatus.COMPLETED,
                    ReservationStatus.CANCELLED
            );
            sort = Sort.by("returnDatetime").descending();
        }

        // Retorna la lista completa ordenada
        List<Reservation> reservations = reservationRepository.findByUserIdAndReservationStatusIn(userId, statuses, sort);

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }

        // Extraer únicamente los UUIDs de las reservas encontradas
        List<UUID> reservationIds = reservations.stream()
                .map(Reservation::getId)
                .collect(Collectors.toList());

        // Hacer una única consulta a la BD para saber cuáles de estas reservas tienen reseña
        Set<UUID> reviewedReservationIds = reviewRepository.findReviewedReservationIds(reservationIds);

        // Transformar Entidades a DTOs
        return reservations.stream()
                .map(reservation -> {
                    UserReservationResponseDTO dto = mapToUserReservationDTO(reservation);
                    // Si el ID de la reserva está en el Set, significa que ya fue calificada
                    dto.setHasReviewed(reviewedReservationIds.contains(reservation.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelReservation(UUID id, String reason, Long userId) {
        logger.info("Intentando cancelar la reserva con ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        //Validación de permisos
        boolean isOwner = reservation.getUser().getId().equals(userId);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(""); //Manejado en el GlobalExceptionHandler - mensaje genérico
        }

        if (reservation.isCanceled()) {
            throw new BookingStateConflictException("No se puede cancelar una reserva que ya está completada o cancelada.");
        }

        FinancialConfigurationResponseDTO config = financialConfigService.getConfiguration();

        // LÓGICA DE PENALIDAD POR CANCELACIÓN
        LocalDateTime now = LocalDateTime.now();
        Double penaltyAmount = calculatePenalty(now, reservation, config);
        reservation.setCancellationPolicyAppliedSnapshot(penaltyAmount);

        reservation.cancel(reason);

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void cancelReservationBySystem(UUID id, String reason) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reservation.isCanceled()) {
            throw new BookingStateConflictException("No se puede cancelar una reserva que ya está completada o cancelada.");
        }

        reservation.cancel(reason);

        // Al guardar, su estado cambia a Cancelled.
        // Automáticamente libera el vehículo y los extras para futuras consultas de fechas.
        reservationRepository.save(reservation);
        logger.info("Reserva {} cancelada por el sistema. Razón: {}", id, reason);
    }

    private void calculatePricing(Reservation res, Vehicle vehicle, FinancialConfigurationResponseDTO config) {

        // A. Cálculo de Días (Mínimo 1 día)
        Long days = res.getRentalDays();

        // B. Base Cost (Días * Precio del Producto/Modelo)
        Double dailyRate = vehicle.getProduct().getPrice();
        res.setBaseCost(dailyRate * days);

        // C. Transfer Fee (Si entrega en otra sucursal)
        Double transferFee = 0.0;
        if (!res.getPickupBranch().getId().equals(res.getReturnBranch().getId())) {
            transferFee = transferFeeRepository
                    .findByOriginBranchIdAndDestinationBranchId(res.getPickupBranch().getId(), res.getReturnBranch().getId())
                    .map(BranchTransferFee::getFeeAmount)
                    .orElse(config.getDefaultTransferFee()); // Costo por defecto si no existe mapeo
        }
        res.setTransferFee(transferFee);

        // D. Insurance Cost (Según el Enum InsuranceType)
        Double insuranceDailyRate = switch (res.getInsuranceType()) {
            case BASIC -> config.getInsuranceBasicRate();
            case PREMIUM -> config.getInsurancePremiumRate();
            case FULL_COVERAGE -> config.getInsuranceFullCoverageRate();
        };
        res.setInsuranceCost(insuranceDailyRate * days);

        // E. Calcular el costo de los extras
        Double extrasCost = 0.0;
        if (res.getExtras() != null) {
            for (ReservationExtra extra : res.getExtras()) {
                Addon addon = extra.getAddon();
                double unitPrice = extra.getUnitPriceSnapshot(); // Precio congelado
                int quantity = extra.getQuantity();

                // Evaluamos la estrategia de cobro
                if (addon.getChargeType() == ChargeType.PER_DAY) {
                    // Si maxChargeableDays es null, no hay límite. Si tiene valor, tomamos el mínimo.
                    long chargeableDays = (addon.getMaxChargeableDays() != null)
                            ? Math.min(days, addon.getMaxChargeableDays())
                            : days;
                    extrasCost += (unitPrice * quantity * chargeableDays);
                } else {
                    // FLAT_FEE (Cobro único)
                    extrasCost += (unitPrice * quantity);
                }
            }
        }
        res.setExtrasCost(extrasCost);

        // F. subtotal Price
        Double subtotal = res.getBaseCost() + res.getTransferFee() + res.getInsuranceCost() + res.getExtrasCost();
        res.setSubtotal(subtotal);

        // G. Tax Amount (Ejemplo: 19% de IVA)
        Double taxRate = config.getTaxRate();
        Double taxAmount = subtotal * taxRate;
        res.setTaxAmount(taxAmount);

        // H. Total Price (Subtotal + Impuestos)
        res.setTotalPrice(subtotal + taxAmount);

        // I. Deposit Amount (Franquicia retenida en tarjeta)
        Double productBaseDeposit = vehicle.getProduct().getBaseDepositAmount();
        Double depositMultiplier = switch (res.getInsuranceType()) {
            case BASIC -> config.getBasicInsuranceDepositMultiplier();
            case PREMIUM -> config.getPremiumInsuranceDepositMultiplier();
            case FULL_COVERAGE -> config.getFullCoverageDepositMultiplier();
        };
        Double finalDeposit = productBaseDeposit * depositMultiplier;
        res.setDepositAmount(finalDeposit);
    }

    private Double calculatePenalty(LocalDateTime now, Reservation reservation, FinancialConfigurationResponseDTO config) {
        LocalDateTime pickup = reservation.getPickupDatetime();
        Double totalPrice = reservation.getTotalPrice();

        // 1. Caso No-Show: Si el cliente no se presentó (ya pasó la hora de recogida)
        // Usamos isAfter o isEqual para mayor seguridad
        if (now.isAfter(pickup) || now.isEqual(pickup)) {
            logger.info("Aplicando penalidad de No-Show para la reserva: {}", reservation.getId());
            return totalPrice * config.getNoShowPenaltyRate();
        }

        // 2. Caso Cancelación Tardía: Usamos la utilidad centralizada
        // Configuración define la ventana en horas (=24h)
        long penaltyWindowHours = config.getPenaltyWindowHours();

        if (DateValidationUtils.isWithinPenaltyWindow(now, pickup, penaltyWindowHours)) {
            logger.info("Aplicando penalidad por cancelación tardía (ventana de {}h)", penaltyWindowHours);
            return totalPrice * config.getCancellationPenaltyRate();
        }

        // 3. Sin penalidad: Cancelación con suficiente antelación
        return 0.0;
    }

    private String generateSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular el hash de la política", e);
        }
    }

    private ReservationResponseDTO mapToReservationDTO(Reservation reservation) {
        long rentalDays = reservation.getRentalDays();

        List<ReservationExtraResponseDTO> extrasDto = reservation.getExtras().stream()
                .map(e -> {
                    Double subtotal = e.getSubtotal(rentalDays);
                    return new ReservationExtraResponseDTO(
                            e.getAddon().getId(),
                            e.getAddon().getName(),
                            e.getQuantity(),
                            e.getUnitPriceSnapshot(),
                            e.getAddon().getChargeType().toString(),
                            e.getAddon().getMaxChargeableDays(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());

        // Sumamos los subtotales de la lista
        Double totalExtras = extrasDto.stream()
                .mapToDouble(ReservationExtraResponseDTO::getSubtotal)
                .sum();

        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getReservationStatus().toString(),
                reservation.getPickupDatetime(),
                reservation.getReturnDatetime(),
                reservation.getPickupBranchNameSnapshot(),
                reservation.getReturnBranchNameSnapshot(),
                reservation.getVehicleLicensePlateSnapshot(),
                reservation.getProductNameSnapshot(),
                reservation.getBaseCost(),
                reservation.getTransferFee(),
                reservation.getInsuranceCost(),
                reservation.getSubtotal(),
                reservation.getTotalPrice(),
                reservation.getPoliciesSnapshot(),
                reservation.getPolicyHash(),
                reservation.getCancellationPolicyAppliedSnapshot(),
                reservation.getFuelLevelAtPickupSnapshot(),
                reservation.getFuelLevelAtReturnSnapshot(),
                reservation.getArrivalFlightNumber(),
                reservation.isUserTheMainDriver(),
                totalExtras,
                extrasDto,
                reservation.getExpirationDate(),
                reservation.getTaxAmount(),
                reservation.getDepositAmount(),
                reservation.getPaymentStatus() != null ? reservation.getPaymentStatus().name() : null,
                reservation.getPaymentGatewayReference()
        );
    }

    // Helper para mapear las reservas del usuario
    private UserReservationResponseDTO mapToUserReservationDTO(Reservation reservation) {
        UserReservationResponseDTO dto = new UserReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setReservationStatus(reservation.getReservationStatus().toString());
        dto.setPickupDatetime(reservation.getPickupDatetime());
        dto.setReturnDatetime(reservation.getReturnDatetime());

        // Snapshots de sucursales si están disponibles
        if (reservation.getPickupBranch() != null) {
            dto.setPickupBranchNameSnapshot(reservation.getPickupBranch().getName());
        }
        if (reservation.getReturnBranch() != null) {
            dto.setReturnBranchNameSnapshot(reservation.getReturnBranch().getName());
        }

        // Obtener la información del vehículo asignado y su modelo/producto
        if (reservation.getVehicle() != null && reservation.getVehicle().getProduct() != null) {
            dto.setProductNameSnapshot(reservation.getVehicle().getProduct().getName());
            dto.setProductId(reservation.getVehicle().getProduct().getId());
            dto.setVehicleLicensePlateSnapshot(reservation.getVehicle().getLicensePlate());
        }

        dto.setTotalPrice(reservation.getTotalPrice());

        return dto;
    }

}

package com.carlikeafriend_backend.backend.entity;

import com.carlikeafriend_backend.backend.util.DateValidationUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_status_dates", columnList = "reservation_status, pickup_datetime, return_datetime"),
        @Index(name = "idx_reservation_availability", columnList = "pickup_branch_id, reservation_status, pickupDatetime, returnDatetime"),
        @Index(name = "idx_reservation_vehicle_dates", columnList = "vehicle_id, reservation_status, pickupDatetime, returnDatetime"),
        @Index(name = "idx_reservation_user", columnList = "user_id"),
        @Index(name = "idx_reservation_user_status_date", columnList = "user_id, reservation_status, pickupDatetime, returnDatetime")
})
public class Reservation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnore
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_branch_id")
    @JsonIgnore
    private Branch pickupBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_branch_id")
    @JsonIgnore
    private Branch returnBranch;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationExtra> extras = new ArrayList<>();

    private LocalDateTime pickupDatetime;
    private LocalDateTime returnDatetime;
    private LocalDateTime reservationDate;
    private String cancellationReason;
    private LocalDateTime cancellationDate;

    private String vehicleLicensePlateSnapshot;
    private String pickupBranchNameSnapshot;
    private String returnBranchNameSnapshot;
    private String productNameSnapshot;
    private Double cancellationPolicyAppliedSnapshot;
    private Integer fuelLevelAtPickupSnapshot;
    private Integer fuelLevelAtReturnSnapshot;

    // --- RENTER DATA SNAPSHOTS (Legal & Historical) ---
    private String renterFullNameSnapshot;
    private String renterEmailSnapshot;
    private String renterPhoneSnapshot;
    private String renterIdNumberTypeSnapshot;
    private String renterIdNumberSnapshot;
    private String renterDriverLicenseSnapshot;
    private LocalDate renterDriverLicenseExpirySnapshot;
    private String renterNationalitySnapshot;
    private String renterCountrySnapshot;
    private String renterStateOrDepartmentSnapshot;
    private String renterCitySnapshot;
    private String renterZipCodeSnapshot;
    private String renterAddressSnapshot;
    private LocalDate renterBirthDateSnapshot;
    private String renterEmergencyContactNameSnapshot;
    private String renterEmergencyContactPhoneSnapshot;

    // SNAPSHOT DE POLÍTICAS
    @Column(length = 16777215) // Genera automáticamente un MEDIUMTEXT
    private String policiesSnapshot; // Almacenamos el texto completo de todas las políticas aceptadas

    private String policyHash;

    private Double baseCost;
    private Double transferFee;
    private Double insuranceCost;
    private Double extrasCost;
    private Double subtotal;
    private Double totalPrice;

    // --- Logística de Viaje ---
    // Si el cliente llega al aeropuerto y el vuelo se retrasa,
    // la sucursal sabe que no debe marcar la reserva como "No Show" (cancelada).
    private String arrivalFlightNumber;

    // --- Conductores Adicionales ---
    // Indica si el usuario logueado es el conductor. Si es false, se debe exigir
    // llenar los datos del conductor principal.
    private boolean isUserTheMainDriver = true;

    // --- Desglose Financiero (Preparación para Pagos) ---
    private Double taxAmount; // Impuestos separados del baseCost
    private Double depositAmount; // El depósito de seguridad/franquicia retenido en la tarjeta

    // ID de la intención de pago o sesión de checkout (Ej. pi_123456789)
    @Column(unique = true)
    private String paymentIntentId;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Referencia externa a la pasarela de pagos (Stripe Session ID, PayPal Order ID)
    private String paymentGatewayReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false)
    private InsuranceType insuranceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inspection> inspections = new ArrayList<>();

    @Version
    private Long version;

    public Reservation() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Branch getPickupBranch() {
        return pickupBranch;
    }

    public void setPickupBranch(Branch pickupBranch) {
        this.pickupBranch = pickupBranch;
    }

    public Branch getReturnBranch() {
        return returnBranch;
    }

    public void setReturnBranch(Branch returnBranch) {
        this.returnBranch = returnBranch;
    }

    public List<ReservationExtra> getExtras() {
        return Collections.unmodifiableList(this.extras);
    }

    private void setExtras(List<ReservationExtra> extras) {
        this.extras = extras;
    }

    public LocalDateTime getPickupDatetime() {
        return pickupDatetime;
    }

    public void setPickupDatetime(LocalDateTime pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }

    public LocalDateTime getReturnDatetime() {
        return returnDatetime;
    }

    public void setReturnDatetime(LocalDateTime returnDatetime) {
        this.returnDatetime = returnDatetime;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getVehicleLicensePlateSnapshot() {
        return vehicleLicensePlateSnapshot;
    }

    public void setVehicleLicensePlateSnapshot(String vehicleLicensePlateSnapshot) {
        this.vehicleLicensePlateSnapshot = vehicleLicensePlateSnapshot;
    }

    public String getPickupBranchNameSnapshot() {
        return pickupBranchNameSnapshot;
    }

    public void setPickupBranchNameSnapshot(String pickupBranchNameSnapshot) {
        this.pickupBranchNameSnapshot = pickupBranchNameSnapshot;
    }

    public String getReturnBranchNameSnapshot() {
        return returnBranchNameSnapshot;
    }

    public void setReturnBranchNameSnapshot(String returnBranchNameSnapshot) {
        this.returnBranchNameSnapshot = returnBranchNameSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public Double getCancellationPolicyAppliedSnapshot() {
        return cancellationPolicyAppliedSnapshot;
    }

    public void setCancellationPolicyAppliedSnapshot(Double cancellationPolicyAppliedSnapshot) {
        this.cancellationPolicyAppliedSnapshot = cancellationPolicyAppliedSnapshot;
    }

    public Integer getFuelLevelAtPickupSnapshot() {
        return fuelLevelAtPickupSnapshot;
    }

    public void setFuelLevelAtPickupSnapshot(Integer fuelLevelAtPickupSnapshot) {
        this.fuelLevelAtPickupSnapshot = fuelLevelAtPickupSnapshot;
    }

    public Integer getFuelLevelAtReturnSnapshot() {
        return fuelLevelAtReturnSnapshot;
    }

    public void setFuelLevelAtReturnSnapshot(Integer fuelLevelAtReturnSnapshot) {
        this.fuelLevelAtReturnSnapshot = fuelLevelAtReturnSnapshot;
    }

    public String getRenterFullNameSnapshot() {
        return renterFullNameSnapshot;
    }

    public void setRenterFullNameSnapshot(String renterFullNameSnapshot) {
        this.renterFullNameSnapshot = renterFullNameSnapshot;
    }

    public String getRenterEmailSnapshot() {
        return renterEmailSnapshot;
    }

    public void setRenterEmailSnapshot(String renterEmailSnapshot) {
        this.renterEmailSnapshot = renterEmailSnapshot;
    }

    public String getRenterPhoneSnapshot() {
        return renterPhoneSnapshot;
    }

    public void setRenterPhoneSnapshot(String renterPhoneSnapshot) {
        this.renterPhoneSnapshot = renterPhoneSnapshot;
    }

    public String getRenterIdNumberTypeSnapshot() {
        return renterIdNumberTypeSnapshot;
    }

    public void setRenterIdNumberTypeSnapshot(String renterIdNumberTypeSnapshot) {
        this.renterIdNumberTypeSnapshot = renterIdNumberTypeSnapshot;
    }

    public String getRenterIdNumberSnapshot() {
        return renterIdNumberSnapshot;
    }

    public void setRenterIdNumberSnapshot(String renterIdNumberSnapshot) {
        this.renterIdNumberSnapshot = renterIdNumberSnapshot;
    }

    public String getRenterDriverLicenseSnapshot() {
        return renterDriverLicenseSnapshot;
    }

    public void setRenterDriverLicenseSnapshot(String renterDriverLicenseSnapshot) {
        this.renterDriverLicenseSnapshot = renterDriverLicenseSnapshot;
    }

    public LocalDate getRenterDriverLicenseExpirySnapshot() {
        return renterDriverLicenseExpirySnapshot;
    }

    public void setRenterDriverLicenseExpirySnapshot(LocalDate renterDriverLicenseExpirySnapshot) {
        this.renterDriverLicenseExpirySnapshot = renterDriverLicenseExpirySnapshot;
    }

    public String getRenterNationalitySnapshot() {
        return renterNationalitySnapshot;
    }

    public void setRenterNationalitySnapshot(String renterNationalitySnapshot) {
        this.renterNationalitySnapshot = renterNationalitySnapshot;
    }

    public String getRenterCountrySnapshot() {
        return renterCountrySnapshot;
    }

    public void setRenterCountrySnapshot(String renterCountrySnapshot) {
        this.renterCountrySnapshot = renterCountrySnapshot;
    }

    public String getRenterStateOrDepartmentSnapshot() {
        return renterStateOrDepartmentSnapshot;
    }

    public void setRenterStateOrDepartmentSnapshot(String renterStateOrDepartmentSnapshot) {
        this.renterStateOrDepartmentSnapshot = renterStateOrDepartmentSnapshot;
    }

    public String getRenterCitySnapshot() {
        return renterCitySnapshot;
    }

    public void setRenterCitySnapshot(String renterCitySnapshot) {
        this.renterCitySnapshot = renterCitySnapshot;
    }

    public String getRenterZipCodeSnapshot() {
        return renterZipCodeSnapshot;
    }

    public void setRenterZipCodeSnapshot(String renterZipCodeSnapshot) {
        this.renterZipCodeSnapshot = renterZipCodeSnapshot;
    }

    public String getRenterAddressSnapshot() {
        return renterAddressSnapshot;
    }

    public void setRenterAddressSnapshot(String renterAddressSnapshot) {
        this.renterAddressSnapshot = renterAddressSnapshot;
    }

    public LocalDate getRenterBirthDateSnapshot() {
        return renterBirthDateSnapshot;
    }

    public void setRenterBirthDateSnapshot(LocalDate renterBirthDateSnapshot) {
        this.renterBirthDateSnapshot = renterBirthDateSnapshot;
    }

    public String getRenterEmergencyContactNameSnapshot() {
        return renterEmergencyContactNameSnapshot;
    }

    public void setRenterEmergencyContactNameSnapshot(String renterEmergencyContactNameSnapshot) {
        this.renterEmergencyContactNameSnapshot = renterEmergencyContactNameSnapshot;
    }

    public String getRenterEmergencyContactPhoneSnapshot() {
        return renterEmergencyContactPhoneSnapshot;
    }

    public void setRenterEmergencyContactPhoneSnapshot(String renterEmergencyContactPhoneSnapshot) {
        this.renterEmergencyContactPhoneSnapshot = renterEmergencyContactPhoneSnapshot;
    }

    public String getPoliciesSnapshot() {
        return policiesSnapshot;
    }

    public void setPoliciesSnapshot(String policiesSnapshot) {
        this.policiesSnapshot = policiesSnapshot;
    }

    public String getPolicyHash() {
        return policyHash;
    }

    public void setPolicyHash(String policyHash) {
        this.policyHash = policyHash;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public Double getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(Double transferFee) {
        this.transferFee = transferFee;
    }

    public Double getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(Double insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public Double getExtrasCost() {
        return extrasCost;
    }

    public void setExtrasCost(Double extraCost) {
        this.extrasCost = extraCost;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getArrivalFlightNumber() {
        return arrivalFlightNumber;
    }

    public void setArrivalFlightNumber(String arrivalFlightNumber) {
        this.arrivalFlightNumber = arrivalFlightNumber;
    }

    public boolean isUserTheMainDriver() {
        return isUserTheMainDriver;
    }

    public void setUserTheMainDriver(boolean userTheMainDriver) {
        isUserTheMainDriver = userTheMainDriver;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentGatewayReference() {
        return paymentGatewayReference;
    }

    public void setPaymentGatewayReference(String paymentGatewayReference) {
        this.paymentGatewayReference = paymentGatewayReference;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public List<Inspection> getInspections() {
        return Collections.unmodifiableList(this.inspections);
    }

    private void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Reservation that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    //Inspección: Metodo de conveniencia añadir/remover
    public void addInspection(Inspection inspection) {
        if (inspection != null && !this.inspections.contains(inspection)) {
            this.inspections.add(inspection);
            if (inspection.getReservation() != this) {
                inspection.setReservation(this);
            }
        }
    }

    public void removeInspection(Inspection inspection) {
        if (inspection != null && this.inspections.contains(inspection)) {
            this.inspections.remove(inspection);
            // Sincronización: Romper la relación inversa
            if (inspection.getReservation() == this) {
                inspection.setReservation(null);
            }
        }
    }

    // --- MÉTODOS DE CONVENIENCIA PARA EXTRAS ---
    public void addExtra(ReservationExtra extra) {
        if (extra != null && !this.extras.contains(extra)) {
            this.extras.add(extra);
            if(extra.getReservation() != this) {
                extra.setReservation(this);
            }
        }
    }

    public void removeExtra(ReservationExtra extra) {
        if (extra != null && this.extras.contains(extra)) {
            this.extras.remove(extra);
            if(extra.getReservation() == this) {
                extra.setReservation(null);
            }
        }
    }

    // Regla 1: No repetir el TIPO de inspección
    public boolean inspectionTypeAlreadyExists(Inspection inspection) {
        return this.inspections.stream()
                .anyMatch(i -> i.getInspectionType() == inspection.getInspectionType());
    }

    // Regla 2: Máximo 2 inspecciones (Entrega y Devolución)
    public boolean isInspectionsComplete() {
        return this.inspections.size() == 2; //"La reserva ya tiene el máximo de inspecciones permitidas (2: Recogida y Entrega)."
    }

    // MÉTODOS DE TRANSICIÓN DE ESTADO (Domain Logic)

    //--CANCELAR RESERVA--
    //Paso 1: Validación
    public boolean isCanceled() {
        return this.reservationStatus == ReservationStatus.CANCELLED || this.reservationStatus == ReservationStatus.COMPLETED;
    }

    //Paso 2: Cambiar estado Cancelled
    public void cancel(String reason) {
        this.reservationStatus = ReservationStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancellationDate = LocalDateTime.now();
    }

    //--CONFIRMAR RESERVA--
    //Paso 1: Validación
    public boolean isConfirmed() {
        return this.reservationStatus == ReservationStatus.CONFIRMED; //"Solo las reservas confirmadas pueden ser iniciadas.";
    }

    //Paso 2: Validación estricta: Requiere inspección de salida
    public boolean hasPickupInspection() {
        return this.inspections.stream()
                .anyMatch(i -> i.getInspectionType().name().equalsIgnoreCase("Pickup")); //"No se puede iniciar el alquiler sin una inspección de entrega (Pickup) registrada."
    }

    //Paso 3: Cambiar estado In_Progress
    public void startRental() {
        if (this.reservationStatus == ReservationStatus.IN_PROGRESS) {
            return; // Idempotencia: Si ya está iniciada, no hacemos nada ni lanzamos error
        }
        this.reservationStatus = ReservationStatus.IN_PROGRESS;
    }

    //--COMPLETAR RESERVA--
    //Paso 1: Validación
    public boolean isInProgress() {
        return this.reservationStatus == ReservationStatus.IN_PROGRESS; //"Solo las reservas en progreso pueden marcarse como completadas.";
    }

    //Paso 2: Validación estricta: Requiere inspección de retorno
    public boolean hasReturnInspection() {
        return this.inspections.stream()
                .anyMatch(i -> i.getInspectionType().name().equalsIgnoreCase("Return"));//"No se puede completar el alquiler sin una inspección de devolución (Return) registrada."
    }

    //Paso 3: Cambiar estado Completed
    public void completeRental() {
        if (this.reservationStatus == ReservationStatus.COMPLETED) {
            return; // Idempotencia
        }
        this.reservationStatus = ReservationStatus.COMPLETED;
    }

    //Cálculo de los días
    public Long getRentalDays() {
       return DateValidationUtils.calculateRentalDays(this.pickupDatetime, this.returnDatetime);
    }

}

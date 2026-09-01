package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DriverDetailsDTO {

    @NotBlank(message = "El nombre completo del conductor es obligatorio")
    @Size(max = 120, message = "El nombre no debe exceder los 120 caracteres")
    private String fullName;

    @NotBlank(message = "El tipo de documento del conductor es obligatorio")
    @Size(max = 35, message = "El tipo de documento del conductor no debe exceder los 35 caracteres")
    private String documentType;

    @NotBlank(message = "El número de documento del conductor es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9\\-]{5,20}$", message = "El número de documento solo puede contener letras, números y guiones (5-20 caracteres)")
    private String documentNumber;

    @NotBlank(message = "El número de teléfono del conductor es obligatorio")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Formato de teléfono inválido (Solo números, 7-15 dígitos)")
    private String phoneNumber;

    @NotBlank(message = "La nacionalidad del conductor es obligatoria")
    @Size(max = 25, message = "La nacionalidad no debe exceder los 25 caracteres")
    private String nationality;

    @NotNull(message = "La fecha de nacimiento del conductor es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado")
    private LocalDate birthDate;

    @NotBlank(message = "La licencia de conducir es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9\\-]{5,20}$", message = "La licencia solo puede contener letras, números y guiones (5-20 caracteres)")
    private String driverLicenseNumber;

    @NotNull(message = "La fecha de expiración de la licencia es obligatoria")
    @FutureOrPresent(message = "La fecha de expiración de la licencia no puede ser en el pasado")
    private LocalDate driverLicenseExpiry;

    @NotBlank(message = "El nombre de contacto de emergencia es obligatorio")
    @Size(max = 100, message = "El nombre del contacto de emergencia no debe exceder los 100 caracteres")
    private String emergencyContactName;

    @NotBlank(message = "El teléfono de contacto de emergencia es obligatorio")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Formato de teléfono inválido (Solo números, 7-15 dígitos)")
    private String emergencyContactPhone;

    public DriverDetailsDTO() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public LocalDate getDriverLicenseExpiry() {
        return driverLicenseExpiry;
    }

    public void setDriverLicenseExpiry(LocalDate driverLicenseExpiry) {
        this.driverLicenseExpiry = driverLicenseExpiry;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
}

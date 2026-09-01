package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserProfileDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no debe exceder los 60 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no debe exceder los 60 caracteres")
    private String lastName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 35, message = "El tipo de documento no debe exceder los 35 caracteres")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9\\-]{5,20}$", message = "El número de documento solo puede contener letras, números y guiones (5-20 caracteres)")
    private String documentNumber;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Formato de teléfono inválido (Solo números, 7-15 dígitos)")
    private String phoneNumber;

    @NotBlank(message = "La nacionalidad es obligatoria")
    @Size(max = 25, message = "La nacionalidad no debe exceder los 25 caracteres")
    private String nationality;

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 2, message = "El país debe ser el código ISO 3166-1 alpha-2 (Ej. CO, US)")
    private String countryCode;

    @NotBlank(message = "El estado o departamento es obligatorio")
    @Size(max = 10, message = "El estado o departamento debe ser el código ISO 3166-2 (Ej. CO-ANT, US-CA)")
    private String stateCode;

    @NotBlank(message = "La ciudad de residencia es obligatoria")
    @Size(max = 100, message = "La ciudad de residencia no debe exceder los 100 caracteres")
    private String city;

    @NotBlank(message = "La dirección de residencia es obligatoria")
    @Size(max = 100, message = "La dirección de residencia no debe exceder los 100 caracteres")
    private String address;

    @NotBlank(message = "El código postal es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9\\s\\-]{3,10}$", message = "El código postal debe tener entre 3 y 10 caracteres alfanuméricos")
    private String zipCode;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado")
    private LocalDate birthDate;

    @NotBlank(message = "La licencia de conducir es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9\\-]{5,20}$", message = "La licencia solo puede contener letras, números y guiones (5-20 caracteres)")
    private String driverLicenseNumber;

    @NotNull(message = "La fecha de expiración de la licencia es obligatoria")
    @FutureOrPresent(message = "La fecha de expiración de la licencia no puede ser en el pasado")
    private LocalDate driverLicenseExpiry;

    @NotBlank(message = "El nombre del contacto de emergencia es obligatorio")
    @Size(max = 100, message = "El nombre del contacto de emergencia no debe excedere los 100 caracteres")
    private String emergencyContactName;

    @NotBlank(message = "El número de contacto de emergencia es obligatorio")
    @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$", message = "Formato de teléfono inválido (Solo números, 7-15 dígitos)")
    private String emergencyContactPhone;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del email es inválido")
    @Size(max = 255, message = "El email no debe exceder los 255 caracteres")
    private String email;


    public UserProfileDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

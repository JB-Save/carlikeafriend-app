package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class UserCompleteResponseDTO {

    private Long id;
    private String name;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String phoneNumber;
    private String nationality;
    private String countryCode;
    private String stateCode;
    private String city;
    private String address;
    private String zipCode;
    private LocalDate birthDate;
    private String driverLicenseNumber;
    private LocalDate driverLicenseExpiry;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String email;
    private List<Long> roleIds;

    public UserCompleteResponseDTO() {
    }

    public UserCompleteResponseDTO(Long id, String name, String lastName, String email, List<Long> roleIds) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.roleIds = roleIds;
    }

    public UserCompleteResponseDTO(Long id,
                                   String name,
                                   String lastName,
                                   String documentType,
                                   String documentNumber,
                                   String phoneNumber,
                                   String nationality,
                                   String countryCode,
                                   String stateCode,
                                   String city,
                                   String address,
                                   String zipCode,
                                   LocalDate birthDate,
                                   String driverLicenseNumber,
                                   LocalDate driverLicenseExpiry,
                                   String emergencyContactName,
                                   String emergencyContactPhone,
                                   String email,
                                   List<Long> roleIds) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.countryCode = countryCode;
        this.stateCode = stateCode;
        this.city = city;
        this.address = address;
        this.zipCode = zipCode;
        this.birthDate = birthDate;
        this.driverLicenseNumber = driverLicenseNumber;
        this.driverLicenseExpiry = driverLicenseExpiry;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.email = email;
        this.roleIds = roleIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}

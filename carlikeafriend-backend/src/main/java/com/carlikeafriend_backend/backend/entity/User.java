package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    @Column(nullable = false)
    private String email;
    @JsonIgnore // ¡BUENA PRÁCTICA! Asegura que nunca se serialice
    private String password;
    private String driverLicenseNumber;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType = DocumentType.CC;
    private String documentNumber;
    private String city;
    private String address;
    private LocalDate birthDate;
    private LocalDate driverLicenseExpiry;

    // --- Geolocalización y Facturación ---
    private String countryCode; // ISO 3166-1 alpha-2 (ej. "CO", "US")
    private String stateCode; // Estado, provincia o departamento ISO 3166-2 (Ej. CO-ANT, US-CA)
    private String zipCode; // CRÍTICO para validación AVS en pasarelas de pago (Stripe, etc.)

    // --- Legal y Operativo ---
    private String nationality; // Requerido por aseguradoras si hay accidentes

    // --- Emergencias ---
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Token del cliente en la pasarela de pagos (Ej. cus_123456789)
    // Se llena la primera vez que el usuario paga algo.
    @Column(unique = true)
    private String stripeCustomerId;

    // Campos para controlar el estado de la cuenta
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserFavorite> favorites = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<ShareInteraction> shareInteractions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
/*
    @OneToMany(mappedBy = "technician", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();

 */

    @Version
    private Long version;

    public User() {
    }

    @Override
    @JsonIgnore // Ignorar también los métodos de UserDetails
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Iterar sobre los roles del usuario
        for (Role role : this.roles) {
            // Añadir el rol (con prefijo ROLE_)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            // Añadir todos los permisos de ESE rol
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }

        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.isEnabled;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDriverLicenseExpiry() {
        return driverLicenseExpiry;
    }

    public void setDriverLicenseExpiry(LocalDate driverLicenseExpiry) {
        this.driverLicenseExpiry = driverLicenseExpiry;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    private void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(this.reservations);
    }

    private void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Set<UserFavorite> getFavorites() {
        return Collections.unmodifiableSet(this.favorites);
    }

    private void setFavorites(Set<UserFavorite> favorites) {
        this.favorites = favorites;
    }

    public List<ShareInteraction> getShareInteractions() {
        return Collections.unmodifiableList(this.shareInteractions);
    }

    private void setShareInteractions(List<ShareInteraction> shareInteractions) {
        this.shareInteractions = shareInteractions;
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(this.reviews);
    }

    private void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
/*
    public List<MaintenanceLog> getMaintenanceLogs() {
        return Collections.unmodifiableList(this.maintenanceLogs);
    }

    private void setMaintenanceLogs(List<MaintenanceLog> maintenanceLogs) {
        this.maintenanceLogs = maintenanceLogs;
    }

 */

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof User that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // --- MÉTODOS DE CONVENIENCIA ---

    // Roles: Colecciones con Set - (Relación User <-> Role)
    public void addRole(Role role) {
        if (role != null && this.roles.add(role)) {
            // Sincronizar el otro lado
            role.addUser(this);
        }
    }

    public void removeRole(Role role) {
        if (role != null && this.roles.remove(role)) {
            // Sincronizar el otro lado
            role.removeUser(this);
        }
    }

    // Reservaciones: Colecciones con List - (Relación User <-> Reservation)
    public void addReservation(Reservation reservation) {
        if (reservation != null && !this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
            // Sincronizar el lado inverso (Reservation)
            if (reservation.getUser() != this) {
                reservation.setUser(this);
            }
        }
    }

    public void removeReservation(Reservation reservation) {
        if (reservation != null && this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            if (reservation.getUser() == this) {
                reservation.setUser(null);
            }
        }
    }

    // Favoritos: Colecciones con Set - (Relación User <-> UserFavorite)
    public void addFavorite(Product product) {
        if (product == null) return;

        UserFavorite favorite = new UserFavorite();
        favorite.setUser(this);
        favorite.setProduct(product);

        if (this.favorites.add(favorite)) {
            product.getFavoritedBy().add(favorite);
        }
    }

    public void removeFavorite(Product product) {
        if (product == null) return;

        // Buscamos el objeto de unión que coincida con el producto y el usuario
        this.favorites.removeIf(f -> f.getProduct().equals(product));
        product.getFavoritedBy().removeIf(f -> f.getUser().equals(this));
    }

    public void clearAllFavorites() {
        // Al limpiar el Set, orphanRemoval = true detecta que los objetos
        // Favorite ya no pertenecen al usuario y los borra de la DB.
        if (this.favorites != null) {
            this.favorites.clear();
        }
    }

    // compartir: Colecciones con List - (Relación User <-> ShareInteraction)
    public void addShareInteraction(ShareInteraction interaction) {
        if (interaction != null) {
            this.shareInteractions.add(interaction);
            interaction.setUser(this); // Sincroniza el otro lado
        }
    }

    public void unlinkShareInteraction(ShareInteraction interaction) {
        if (interaction != null) {
            this.shareInteractions.remove(interaction);
            interaction.setUser(null); // Desvincula el otro lado
        }
    }

    // review: Colecciones con List - (Relación User <-> Review)
    public void addReview(Review review) {
        if (review != null) {
            this.reviews.add(review);
            review.setUser(this); // Sincroniza el otro lado
        }
    }

    public void removeReview(Review review) {
        if (review != null) {
            this.reviews.remove(review);
            review.setUser(null); // Desvincula el otro lado
        }
    }
/*
    // MaintenanceLog: Colecciones con List - (Relación User <-> MaintenanceLog)
    public void addMaintenanceLog(MaintenanceLog log) {
        if (log != null && !this.maintenanceLogs.contains(log)) {
            this.maintenanceLogs.add(log);
            if (log.getTechnician() != this) {
                log.setTechnician(this);
            }
        }
    }

    public void removeMaintenanceLog(MaintenanceLog log) {
        if (log != null && this.maintenanceLogs.contains(log)) {
            this.maintenanceLogs.remove(log);
            if (log.getTechnician() == this) {
                log.setTechnician(null);
            }
        }
    }

 */

    // Método hook de JPA: Se ejecuta AUTOMÁTICAMENTE antes de borrar el Usuario
    @PreRemove
    public void unlinkAllInteractionsBeforeDelete() {
        // Recorremos una copia de la lista para evitar ConcurrentModificationException
        for (ShareInteraction interaction : new ArrayList<>(shareInteractions)) {
            unlinkShareInteraction(interaction);
        }
    }

    // Método de Validación Reservas pendientes
    public boolean hasPendingReservations() {
        // Regla 1: No tener reservas pendientes
        return this.reservations.stream()
                .anyMatch(r -> r.getReservationStatus() != ReservationStatus.COMPLETED && r.getReservationStatus() != ReservationStatus.CANCELLED);
    }

    // Método de Validación Único Admin
    public boolean isLastAdmin(int totalAdmins) {
        // Regla 2: No ser el último ADMIN
        boolean isAdmin = this.roles.stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        return isAdmin && totalAdmins <= 1;
    }

    // Método para aplicar los cambios de negocio (Anonimización y Desactivación)
    public void prepareForDeletion() {
        // Regla 3: Evaluar si es un usuario normal (solo tiene el rol USER)
        boolean isOnlyCustomer = this.roles.size() == 1 && this.roles.stream().anyMatch(r -> "USER".equals(r.getName()));

        if (isOnlyCustomer) {
            anonymizeData();
        }
        // Todos los usuarios borrados (sean ADMIN, USER u otros) deben quedar inactivos
        this.isEnabled = false;
    }

    // 3. La anonimización (Privada, solo llamada por prepareForDeletion)
    private void anonymizeData() {
        this.name = "USUARIO";
        this.lastName = "ELIMINADO";
        // IMPORTANTE: Añadir un UUID o timestamp al email evita que la base de datos
        // lance error de Unique Constraint si anonimizas varios usuarios
        this.email = "anonimo_" + UUID.randomUUID().toString().substring(0, 8) + "_" + this.id + "@carlikeafriend.com";
        this.password = "********";
        this.driverLicenseNumber = "********";
        this.phoneNumber = "********";
        this.documentNumber = "********";
        this.address = "********";
        this.emergencyContactName = "********";
        this.emergencyContactPhone = "********";
        this.zipCode = "********";
        this.stripeCustomerId = "********";
    }

    public boolean isProfileComplete() {
        return name != null && !name.isBlank() &&
                lastName != null && !lastName.isBlank() &&
                email != null && !email.isBlank() &&
                driverLicenseNumber != null && !driverLicenseNumber.isBlank() &&
                phoneNumber != null && !phoneNumber.isBlank() &&
                documentType != null &&
                documentNumber != null && !documentNumber.isBlank() &&
                city != null && !city.isBlank() &&
                address != null && !address.isBlank() &&
                birthDate != null &&
                driverLicenseExpiry != null &&
                countryCode != null && !countryCode.isBlank() &&
                stateCode != null && !stateCode.isBlank() &&
                nationality != null && !nationality.isBlank() &&
                zipCode != null && !zipCode.isBlank() &&
                emergencyContactName != null && !emergencyContactName.isBlank() &&
                emergencyContactPhone != null && !emergencyContactPhone.isBlank();
    }

}

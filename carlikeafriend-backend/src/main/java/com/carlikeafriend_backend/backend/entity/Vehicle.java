package com.carlikeafriend_backend.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vehicle", uniqueConstraints = {
        @UniqueConstraint(columnNames = "licensePlate") // Asegura que la placa del vehículo sea único
}, indexes = {
        @Index(name = "idx_vehicle_availability", columnList = "product_id, current_branch_id, vehicle_status, deleted")
})
public class Vehicle extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // La placa no puede ser nula
    private String licensePlate;

    @Column(unique = true, nullable = false)
    private String vin;

    private Integer currentMileage;
    private String color;
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_branch_id")
    @JsonIgnore
    private Branch currentBranch;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_status", nullable = false)
    private VehicleStatus vehicleStatus;

    @OneToMany(mappedBy = "vehicle", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @Version // Campo para el bloqueo optimista
    private Long version;

    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(Branch currentBranch) {
        this.currentBranch = currentBranch;
    }

    public List<MaintenanceLog> getMaintenanceLogs() {
        // Retorna una vista de solo lectura.
        // Si alguien hace .add() o .remove() en este resultado,
        // Java lanzará una UnsupportedOperationException inmediatamente.
        return Collections.unmodifiableList(this.maintenanceLogs);
    }

    private void setMaintenanceLogs(List<MaintenanceLog> maintenanceLogs) {
        this.maintenanceLogs = maintenanceLogs;
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(this.reservations);
    }

    private void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(this.reviews);
    }

    private void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        // 1. Comparación de referencia física
        if (this == o) return true;

        // 2. Verificación de clase (usamos instanceof para ser compatibles con Proxies)
        if (!(o instanceof Vehicle that)) return false;

        // 3. Si el ID es nulo, los objetos no son iguales (a menos que sean la misma instancia)
        // Esto es vital para objetos nuevos que aún no se han guardado
        if (this.id == null || that.getId() == null) {
            return false;
        }

        // 4. Comparación lógica por Identificador Único
        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        // Retornamos un valor constante para cumplir el contrato de Hash
        // y evitar que el objeto se "pierda" en un Set tras persistirlo.
        return getClass().hashCode();
    }

    // --- MÉTODOS DE CONVENIENCIA ---

    // Mantenimiento (OneToMany)
    public void addMaintenanceLog(MaintenanceLog log) {
        if (log != null && !this.maintenanceLogs.contains(log)) {
            this.maintenanceLogs.add(log);
            if (log.getVehicle() != this) {
                log.setVehicle(this);
            }
        }
    }

    public void removeMaintenanceLog(MaintenanceLog log) {
        if (log != null && this.maintenanceLogs.contains(log)) {
            this.maintenanceLogs.remove(log);
            // Sincronización: Romper la relación inversa
            if (log.getVehicle() == this) {
                log.setVehicle(null);
            }
        }
    }

    // Reservación (OneToMany)
    public void addReservation(Reservation reservation) {
        if (reservation != null && !this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
            if (reservation.getVehicle() != this) {
                reservation.setVehicle(this);
            }
        }
    }

    public void removeReservation(Reservation reservation) {
        if (reservation != null && this.reservations.contains(reservation)) {
            this.reservations.remove(reservation);
            // Sincronización: Romper la relación inversa
            if (reservation.getVehicle() == this) {
                reservation.setVehicle(null);
            }
        }
    }

    // Reviews (OneToMany)
    public void addReview(Review review) {
        if (review != null) {
            this.reviews.add(review);
            review.setVehicle(this); // Sincroniza el otro lado
        }
    }

    /*// Método para desvincular UNA review (sin borrarla de la BD)
    public void unlinkReview(Review review) {
        if (review != null) {
        // 1. Remover de la lista local para consistencia en memoria
        this.reviews.remove(review);

        // 2. IMPORTANTE: No dejarlo huérfano, sino setear NULL explícitamente
            review.setVehicle(null);
        }
    }

    // Método hook de JPA: Se ejecuta AUTOMÁTICAMENTE antes de borrar el Vehículo
    @PreRemove
    public void unlinkAllReviewsBeforeDelete() {
        // Recorremos una copia de la lista para evitar ConcurrentModificationException
        for (Review review : new ArrayList<>(reviews)) {
            unlinkReview(review);
        }
    }
*/
    public boolean isOutOfService(){
        // Regla 1: Estado del vehículo en Out_of_Service
        return this.vehicleStatus == VehicleStatus.OUT_OF_SERVICE;
    }

    //Método para borrado Lógico de Vehicle
    public boolean hasPendingReservations() {
        // Regla 2: No tener Reservas vigentes
        return this.reservations.stream()
                .anyMatch(r -> r.getReservationStatus() == ReservationStatus.CONFIRMED || r.getReservationStatus() == ReservationStatus.IN_PROGRESS);
    }

    // --- LÓGICA DE DOMINIO PARA MANTENIMIENTO ---

    //--Enviar a Mantenimiento--
    //Paso 1: Validación
    public boolean isRented(){
        return this.vehicleStatus == VehicleStatus.RENTED;
    }

    //Paso 2: Cambiar estado a Maintenance
    public void sendToMaintenance() {
        if (this.vehicleStatus == VehicleStatus.MAINTENANCE) {
            return; // Idempotencia
        }
        this.vehicleStatus = VehicleStatus.MAINTENANCE;
    }

    //--Terminar Mantenimiento--
    //Paso 1: Validación
    public boolean isInMaintenance(){
        return this.vehicleStatus == VehicleStatus.MAINTENANCE;
    }

    //Paso 2: Liberar Vehículo
    public void release() {
        if (this.vehicleStatus == VehicleStatus.AVAILABLE) {
            return; // Idempotencia
        }
        this.vehicleStatus = VehicleStatus.AVAILABLE;
    }

    //--Alquilar Vehículo--
    public void rentOut() {
        if (this.vehicleStatus == VehicleStatus.RENTED) {
            return; // Idempotencia
        }
        this.vehicleStatus = VehicleStatus.RENTED;
    }

}

package com.carlikeafriend_backend.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "branch", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Branch extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    @JsonIgnore
    private City city;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private  BigDecimal longitude;

    @OneToMany(mappedBy = "currentBranch", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();

    // Relación de Salida (Tarifa)
    @OneToMany(mappedBy = "originBranch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BranchTransferFee> outgoingTransferFees = new ArrayList<>();

    // Relación de Entrada (Tarifa)
    @OneToMany(mappedBy = "destinationBranch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BranchTransferFee> incomingTransferFees = new ArrayList<>();

    // Relación de Salida (Reserva)
    @OneToMany(mappedBy = "pickupBranch", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Reservation> pickupReservations = new ArrayList<>();

    // Relación de Entrada (Reserva)
    @OneToMany(mappedBy = "returnBranch", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Reservation> returnReservations = new ArrayList<>();

    @Version
    private Long version;

    public Branch() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
            this.city = city;
           }

    public  BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude( BigDecimal latitude) {
        this.latitude = latitude;
    }

    public  BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude( BigDecimal longitude) {
        this.longitude = longitude;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(this.vehicles);
    }

    private void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<BranchTransferFee> getOutgoingTransferFees() {
        return Collections.unmodifiableList(this.outgoingTransferFees);
    }

    private void setOutgoingTransferFees(List<BranchTransferFee> outgoingTransferFees) {
        this.outgoingTransferFees = outgoingTransferFees;
    }

    public List<BranchTransferFee> getIncomingTransferFees() {
        return Collections.unmodifiableList(this.incomingTransferFees);
    }

    private void setIncomingTransferFees(List<BranchTransferFee> incomingTransferFees) {
        this.incomingTransferFees = incomingTransferFees;
    }

    public List<Reservation> getPickupReservations() {
        return Collections.unmodifiableList(this.pickupReservations);
    }

    private void setPickupReservations(List<Reservation> pickupReservations) {
        this.pickupReservations = pickupReservations;
    }

    public List<Reservation> getReturnReservations() {
        return Collections.unmodifiableList(this.returnReservations);
    }

    private void setReturnReservations(List<Reservation> returnReservations) {
        this.returnReservations = returnReservations;
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

        if (!(o instanceof Branch that)) return false;

        if (this.id == null || that.getId() == null) {
            return false;
        }

        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Métodos de conveniencia para tarifas

    // --- Relación de Salida (Origin) ---
    public void addOutgoingTransferFee(BranchTransferFee fee) {
        if (fee != null && !this.outgoingTransferFees.contains(fee)) {
            this.outgoingTransferFees.add(fee);
            if(fee.getOriginBranch() != this) {
                fee.setOriginBranch(this);
            }
        }
    }

    // 1. Borrado desde el lado del ORIGEN
    public void removeOutgoingTransferFee(BranchTransferFee fee) {
        if (fee != null && this.outgoingTransferFees.contains(fee)) {
            this.outgoingTransferFees.remove(fee);
            // Desvincula el lado dueño de la relación
            if (fee.getOriginBranch() == this) {
                fee.setOriginBranch(null);
            }
        }
    }

    // --- Relación de Entrada (Destination) ---
    public void addIncomingTransferFee(BranchTransferFee fee) {
        if (fee != null && !this.incomingTransferFees.contains(fee)) {
            this.incomingTransferFees.add(fee);
            if(fee.getDestinationBranch() != this) {
                fee.setDestinationBranch(this);
            }
        }
    }

    // 2. Borrado desde el lado del DESTINO
    public void removeIncomingTransferFee(BranchTransferFee fee) {
        if (fee != null && this.incomingTransferFees.contains(fee)) {
            this.incomingTransferFees.remove(fee);
            // Desvincula el lado dueño de la relación
            if (fee.getDestinationBranch() == this) {
                fee.setDestinationBranch(null);
            }
        }
    }


    // Métodos de conveniencia para reservas

    // --- Relación de Salida (Pickup) ---
    public void addPickupBranch(Reservation reservation) {
        if (reservation != null && !this.pickupReservations.contains(reservation)) {
            this.pickupReservations.add(reservation);
            if (reservation.getPickupBranch() != this) {
                reservation.setPickupBranch(this);
            }
        }
    }

    // 1. Borrado desde el lado del ORIGEN
    public void removePickupBranch(Reservation reservation) {
        if (reservation != null && this.pickupReservations.contains(reservation)) {
            this.pickupReservations.remove(reservation);
            // Desvincular la reserva de esta sucursal (Origen)
            if (reservation.getPickupBranch() == this) {
                reservation.setPickupBranch(null);
            }
        }
    }

    // --- Relación de Entrada (return) ---
    public void addReturnBranch(Reservation reservation) {
        if (reservation != null && !this.returnReservations.contains(reservation)) {
            this.returnReservations.add(reservation);
            if (reservation.getReturnBranch() != this) {
                reservation.setReturnBranch(this);
            }
        }
    }

    // 2. Borrado desde el lado del DESTINO
    public void removeReturnBranch(Reservation reservation) {
        if (reservation != null && this.returnReservations.contains(reservation)) {
            this.returnReservations.remove(reservation);
            // Desvincular la reserva de esta sucursal (Destino)
            if (reservation.getReturnBranch() == this) {
                reservation.setReturnBranch(null);
            }
        }
    }

    // Vehículo: Métodos de conveniencia
    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null && !this.vehicles.contains(vehicle)) {
            this.vehicles.add(vehicle);
            if (vehicle.getCurrentBranch() != this) {
                vehicle.setCurrentBranch(this);
            }
        }
    }

    public void removeVehicle(Vehicle vehicle) {
        if (vehicle != null && this.vehicles.contains(vehicle)) {
            this.vehicles.remove(vehicle);
            if (vehicle.getCurrentBranch() == this) {
                vehicle.setCurrentBranch(null);
            }
        }
    }

    //Método para borrado Lógico de Branch
    public boolean hasPendingReservations() {
        // Regla 1: No tener Reservas vigentes
       return this.returnReservations.stream()
                .anyMatch(r -> r.getReservationStatus() != ReservationStatus.COMPLETED && r.getReservationStatus() != ReservationStatus.CANCELLED);
    }

    public boolean hasActiveVehicles(){
        // Regla 2: No tener vehículos asociados
        return this.vehicles.stream().anyMatch(v -> !v.isDeleted());
    }

}

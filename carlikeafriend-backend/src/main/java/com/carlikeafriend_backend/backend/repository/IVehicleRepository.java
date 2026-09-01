package com.carlikeafriend_backend.backend.repository;


import com.carlikeafriend_backend.backend.entity.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    /**
     * Métodos de consulta que incluyen el estado: deleted (Borrado Lógico)
     **/
    // Validaciones de duplicados
    boolean existsByLicensePlateAndDeletedFalse(String licensePlate);

    boolean existsByVinAndDeletedFalse(String vin);

    boolean existsByLicensePlateAndIdNotAndDeletedFalse(String licensePlate, Long id);

    boolean existsByVinAndIdNotAndDeletedFalse(String vin, Long id);

    // Consultas estándar: Ignoran los borrados
    Optional<Vehicle> findByIdAndDeletedFalse(Long id);

    List<Vehicle> findAllByDeletedFalse();

    // Contar cuántos vehículos de un producto específico existen y están disponibles en una sucursal dada
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.product.id = :productId " +
            "AND v.currentBranch.id = :branchId AND v.deleted = false AND v.vehicleStatus = 'AVAILABLE'")
    int countAvailableVehiclesByProductIdAndBranchId(
            @Param("productId") Long productId,
            @Param("branchId") Long branchId
    );

    //Si otro proceso intenta ejecutar este mismo método para el mismo ID, la base de datos lo pondrá en "espera" automáticamente.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.deleted = false")
    Optional<Vehicle> findByIdWithLock(@Param("id") Long id);

    // Búsqueda histórica: Para encontrar el registro y restaurarlo
    // Usamos LIKE porque la placa en la BD tendrá el sufijo _DELETE_...
    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate LIKE :platePrefix% AND v.deleted = true")
    Optional<Vehicle> findDeletedByLicensePlatePrefix(@Param("platePrefix") String platePrefix);

    //busca los vehículos de un producto que están en la sucursal de origen y no tienen solapamientos en esas fechas
    @Query("SELECT v.id FROM Vehicle v WHERE v.product.id = :productId " +
            "AND v.currentBranch.id = :branchId " +
            "AND v.deleted = false AND v.vehicleStatus NOT IN ('OUT_OF_SERVICE', 'MAINTENANCE') " +
            "AND NOT EXISTS (SELECT r FROM Reservation r WHERE r.vehicle = v " +
            "AND r.reservationStatus NOT IN ('CANCELLED', 'COMPLETED') " +
            "AND (r.pickupDatetime < :returnDate AND r.returnDatetime > :pickupDate))")
    List<Long> findAvailableVehicleIdsForProduct(
            @Param("productId") Long productId,
            @Param("branchId") Long branchId,
            @Param("pickupDate") LocalDateTime pickupDate,
            @Param("returnDate") LocalDateTime returnDate);
}

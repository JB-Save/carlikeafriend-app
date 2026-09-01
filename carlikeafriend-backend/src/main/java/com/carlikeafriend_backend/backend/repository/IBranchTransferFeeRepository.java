package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.BranchTransferFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBranchTransferFeeRepository extends JpaRepository<BranchTransferFee, Long> {

    boolean existsByOriginBranchIdAndDestinationBranchId(Long originBranchId, Long destinationBranchId);

    boolean existsByOriginBranchIdAndDestinationBranchIdAndIdNot(Long originBranchId, Long destinationBranchId, Long id);
    Optional<BranchTransferFee> findByOriginBranchIdAndDestinationBranchId(Long pickupBranchId, Long returnBranchId);

    @Query("SELECT btf FROM BranchTransferFee btf " +
            "JOIN FETCH btf.originBranch ob " +
            "JOIN FETCH btf.destinationBranch db " +
            "WHERE ob.id = :originBranchId " +
            "AND db.deleted = false")
    List<BranchTransferFee> findActiveFeesByOriginBranchId(@Param("originBranchId") Long originBranchId);
}

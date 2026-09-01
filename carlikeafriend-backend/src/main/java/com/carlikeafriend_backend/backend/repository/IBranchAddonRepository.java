package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.BranchAddon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBranchAddonRepository extends JpaRepository<BranchAddon, Long> {

    Optional<BranchAddon> findByBranchIdAndAddonId(Long branchId, Long addonId);

    @Query("SELECT ba FROM BranchAddon ba " +
            "JOIN FETCH ba.addon a " +
            "WHERE ba.branch.id = :branchId " +
            "AND a.deleted = false")
    List<BranchAddon> findActiveAddonsByBranchId(@Param("branchId") Long branchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ba FROM BranchAddon ba WHERE ba.branch.id = :branchId AND ba.addon.id = :addonId")
    Optional<BranchAddon> findByBranchIdAndAddonIdWithLock(@Param("branchId") Long branchId, @Param("addonId") Long addonId);

}

package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAddonRepository extends JpaRepository<Addon, Long> {
    boolean existsByNameAndDeletedFalse(String name);
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);
    Optional<Addon> findByIdAndDeletedFalse(Long id);
    List<Addon> findAllByDeletedFalse();
}

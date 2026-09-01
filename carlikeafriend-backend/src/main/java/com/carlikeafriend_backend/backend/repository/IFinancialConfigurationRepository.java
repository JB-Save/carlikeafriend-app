package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.FinancialConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFinancialConfigurationRepository extends JpaRepository<FinancialConfiguration, Long> {
}

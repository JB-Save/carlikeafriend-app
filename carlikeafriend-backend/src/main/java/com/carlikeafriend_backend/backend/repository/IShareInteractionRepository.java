package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.ShareInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IShareInteractionRepository extends JpaRepository<ShareInteraction, Long> {
}

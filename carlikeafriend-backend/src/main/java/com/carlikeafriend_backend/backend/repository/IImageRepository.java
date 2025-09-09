package com.carlikeafriend_backend.backend.repository;

import com.carlikeafriend_backend.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IImageRepository extends JpaRepository<Image, Long> {
}

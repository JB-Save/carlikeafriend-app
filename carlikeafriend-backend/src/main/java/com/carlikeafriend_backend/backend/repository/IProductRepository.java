package com.carlikeafriend_backend.backend.repository;


import com.carlikeafriend_backend.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

    //Consulta respetando la convención de Spring Data con los nombres de método
    Optional<Product> findByName(String name); // Método para buscar por nombre
}

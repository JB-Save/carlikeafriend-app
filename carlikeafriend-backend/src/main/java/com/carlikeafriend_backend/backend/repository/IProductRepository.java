package com.carlikeafriend_backend.backend.repository;


import com.carlikeafriend_backend.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Método que verifica si existe un producto con un nombre específico.
    boolean existsByName(String name);

    // Método que verifica si existe una producto con un nombre, excluyendo un ID específico.
    boolean existsByNameAndIdNot(String name, Long id);
}

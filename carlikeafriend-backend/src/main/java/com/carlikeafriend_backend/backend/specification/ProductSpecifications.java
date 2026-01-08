package com.carlikeafriend_backend.backend.specification;

import com.carlikeafriend_backend.backend.entity.Category;
import com.carlikeafriend_backend.backend.entity.Feature;
import com.carlikeafriend_backend.backend.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {

    // 1. Especificación para pertenecer a TODAS las categorías seleccionadas
    public static Specification<Product> inAllCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            // Se utiliza una subconsulta para la lógica del "HAVING COUNT"
            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Product> subRoot = subquery.from(Product.class);
            Join<Product, Category> joinCategory = subRoot.join("categories"); // Navegación por la relación mapeada

            subquery.select(subRoot.get("id")); // Selecciona el ID del producto

            // Filtra por los IDs de categorías proporcionados
            subquery.where(joinCategory.get("id").in(categoryIds));

            // Agrupa por producto y aplica el HAVING COUNT
            subquery.groupBy(subRoot.get("id"));
            subquery.having(criteriaBuilder.equal(
                    criteriaBuilder.count(joinCategory.get("id")), // Cuenta las categorías únicas
                    (long) categoryIds.size() // Compara con el número total de categorías requeridas
            ));

            // El producto principal debe estar en los resultados de la subconsulta
            return root.get("id").in(subquery);
        };
    }

    // 2. Especificación para pertenecer a TODAS las características seleccionadas
    public static Specification<Product> inAllFeatures(List<Long> featureIds) {
        return (root, query, criteriaBuilder) -> {
            // Lógica similar a la de categorías, pero para características
            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Product> subRoot = subquery.from(Product.class);
            Join<Product, Feature> joinFeature = subRoot.join("features");

            subquery.select(subRoot.get("id"));
            subquery.where(joinFeature.get("id").in(featureIds));
            subquery.groupBy(subRoot.get("id"));
            subquery.having(criteriaBuilder.equal(
                    criteriaBuilder.count(joinFeature.get("id")),
                    (long) featureIds.size()
            ));

            return root.get("id").in(subquery);
        };
    }

    // 3. Especificación para el rango de precios
    public static Specification<Product> priceBetween(double min, double max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), min, max);
    }


    /* 4. Lógica de Ordenamiento
     Recibe un string con el formato "campo_direccion" (ej: "price_asc", "name_desc").
     Desestructura el string para obtener el campo y la dirección.
     Por defecto ordena por 'price' ascendente.
    */
    public static Sort createSort(String sortCriteria) {

        String field = "price"; // Campo por defecto
        Sort.Direction direction = Sort.Direction.ASC; // Dirección por defecto

        if (sortCriteria != null && !sortCriteria.trim().isEmpty()) {
            // Separamos el string por el guion bajo "_"
            String[] parts = sortCriteria.split("_");

            // Asignamos el campo (parte 0), ej: "price"
            if (parts.length > 0 && !parts[0].isEmpty()) {
                field = parts[0];
            }

            // Asignamos la dirección si existe (parte 1), ej: "desc"
            if (parts.length > 1) {
                if ("desc".equalsIgnoreCase(parts[1])) {
                    direction = Sort.Direction.DESC;
                }
            }
        }

        return Sort.by(direction, field);
    }

}

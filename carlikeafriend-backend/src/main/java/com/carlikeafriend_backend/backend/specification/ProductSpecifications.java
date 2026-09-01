package com.carlikeafriend_backend.backend.specification;

import com.carlikeafriend_backend.backend.entity.*;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class ProductSpecifications {

    // Regla base innegociable: El producto no debe estar marcado como borrado
    public static Specification<Product> base() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }

    // 1. Disponibilidad global
    public static Specification<Product> isGloballyAvailable() {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            Subquery<Long> vehicleSubquery = query.subquery(Long.class);
            Root<Vehicle> vehicleRoot = vehicleSubquery.from(Vehicle.class);

            Join<Vehicle, Branch> branchJoin = vehicleRoot.join("currentBranch");

            vehicleSubquery.select(vehicleRoot.get("product").get("id"));
            vehicleSubquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.isFalse(vehicleRoot.get("deleted")),
                            criteriaBuilder.equal(vehicleRoot.get("vehicleStatus"), VehicleStatus.AVAILABLE),
                            criteriaBuilder.isFalse(branchJoin.get("deleted"))
                    )
            );

            return root.get("id").in(vehicleSubquery);
        };
    }

    // 2. Especificación para pertenecer a TODAS las categorías seleccionadas
    public static Specification<Product> inAllCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            // Se utiliza una subconsulta para la lógica del "HAVING COUNT"
            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Product> subRoot = subquery.from(Product.class);
            Join<Product, Category> joinCategory = subRoot.join("categories"); // Navegación por la relación mapeada

            subquery.select(subRoot.get("id")); // Selecciona el ID del producto

            // Validar el ID y que la categoría no esté borrada lógicamente
            subquery.where(
                    criteriaBuilder.and(
                            joinCategory.get("id").in(categoryIds),
                            criteriaBuilder.isFalse(joinCategory.get("deleted"))
                    )
            );

            // Agrupa por producto y aplica el HAVING COUNT
            subquery.groupBy(subRoot.get("id"));
            subquery.having(criteriaBuilder.equal(
                    criteriaBuilder.countDistinct(joinCategory.get("id")), // Cuenta las categorías únicas
                    (long) categoryIds.size() // Compara con el número total de categorías requeridas
            ));

            // El producto principal debe estar en los resultados de la subconsulta
            return root.get("id").in(subquery);
        };
    }

    // 3. Especificación para pertenecer a TODAS las características seleccionadas
    public static Specification<Product> inAllFeatures(List<Long> featureIds) {
        return (root, query, criteriaBuilder) -> {
            // Lógica similar a la de categorías, pero para características
            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Product> subRoot = subquery.from(Product.class);
            Join<Product, Feature> joinFeature = subRoot.join("features");

            subquery.select(subRoot.get("id"));

            // Validar el ID y que la característica no esté borrada lógicamente
            subquery.where(
                    criteriaBuilder.and(
                            joinFeature.get("id").in(featureIds),
                            criteriaBuilder.isFalse(joinFeature.get("deleted"))
                    )
            );

            subquery.groupBy(subRoot.get("id"));
            subquery.having(criteriaBuilder.equal(
                    criteriaBuilder.countDistinct(joinFeature.get("id")),
                    (long) featureIds.size()
            ));

            return root.get("id").in(subquery);
        };
    }

    // 4. Especificación para el rango de precios
    public static Specification<Product> priceBetween(double min, double max) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), min, max);
    }

    // 5. Disponibilidad en sucursal y fechas
    public static Specification<Product> isAvailableInBranchAndDates(Long branchId, LocalDateTime pickupDate, LocalDateTime returnDate) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            Subquery<Long> vehicleSubquery = query.subquery(Long.class);
            Root<Vehicle> vehicleRoot = vehicleSubquery.from(Vehicle.class);

            // Unimos con Branch para verificar su estado
            Join<Vehicle, Branch> branchJoin = vehicleRoot.join("currentBranch");

            // Sub-Subconsulta para buscar reservas conflictivas
            Subquery<Long> reservationSubquery = vehicleSubquery.subquery(Long.class);
            Root<Reservation> reservationRoot = reservationSubquery.from(Reservation.class);

            reservationSubquery.select(reservationRoot.get("id"));
            // Condición: La reserva se solapa con las fechas solicitadas y no está cancelada/completada
            reservationSubquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(reservationRoot.get("vehicle"), vehicleRoot),
                            // Si la reserva está borrada lógicamente, NO debe bloquear el vehículo
                            criteriaBuilder.isFalse(reservationRoot.get("deleted")),
                            criteriaBuilder.not(reservationRoot.get("reservationStatus").in(ReservationStatus.CANCELLED, ReservationStatus.COMPLETED)),
                            criteriaBuilder.lessThan(reservationRoot.get("pickupDatetime"), returnDate),
                            criteriaBuilder.greaterThan(reservationRoot.get("returnDatetime"), pickupDate)
                    )
            );

            // Seleccionamos el ID del producto al que pertenece el vehículo
            vehicleSubquery.select(vehicleRoot.get("product").get("id"));

            // Condiciones del vehículo: Activo, en la ciudad deseada y SIN reservas conflictivas
            vehicleSubquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.isFalse(vehicleRoot.get("deleted")),
                            //Verificar que la sucursal NO esté borrada
                            criteriaBuilder.equal(branchJoin.get("id"), branchId),
                            criteriaBuilder.isFalse(branchJoin.get("deleted")),
                            criteriaBuilder.not(criteriaBuilder.exists(reservationSubquery))
                    )
            );

            // El producto debe existir en la lista de vehículos disponibles
            return root.get("id").in(vehicleSubquery);
        };
    }


    /*  6. Filtro Dinámico de Precio:
      Calcula el costo total directamente en la consulta SQL usando funciones matemáticas
      del motor de base de datos para no romper la paginación de JPA.
     */
    public static Specification<Product> dynamicPriceBetween(
            Double minTotalBudget,
            Double maxTotalBudget,
            long rentalDays,
            double constantFees, // Transferencias + Seguros
            double taxRate       // Ej: 0.19
    ) {
        return (root, query, criteriaBuilder) -> {

            // 1. Casting seguro de los valores para CriteriaBuilder
            Expression<Double> priceExpr = root.get("price").as(Double.class);
            Expression<Double> daysExpr = criteriaBuilder.literal((double) rentalDays);
            Expression<Double> constantsExpr = criteriaBuilder.literal(constantFees);

            // Tasa de impuesto como multiplicador (Ej: 19% IVA = 1.19)
            Expression<Double> taxMultiplierExpr = criteriaBuilder.literal(1.0 + taxRate);

            // 2. Costo Base = (p.price * rentalDays)
            Expression<Double> baseCost = criteriaBuilder.prod(priceExpr, daysExpr);

            // 3. Subtotal = Costo Base + Costos Fijos
            Expression<Double> subtotal = criteriaBuilder.sum(baseCost, constantsExpr);

            // 4. Total = Subtotal * (1 + taxRate)
            Expression<Double> totalCost = criteriaBuilder.prod(subtotal, taxMultiplierExpr);

            // 5. Aplicar el filtro BETWEEN sobre el resultado de la ecuación
            return criteriaBuilder.between(totalCost, minTotalBudget, maxTotalBudget);
        };
    }

    /* 7. Lógica de Ordenamiento
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

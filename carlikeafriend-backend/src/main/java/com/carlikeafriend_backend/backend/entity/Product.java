package com.carlikeafriend_backend.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"), // Asegura que el nombre del producto sea único
}, indexes = {
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_make", columnList = "make_id"),
        @Index(name = "idx_product_deleted", columnList = "deleted")
})
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // El nombre no puede ser nulo
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "make_id")
    @JsonIgnore
    private Make make;

    @Column(length = 65535) // Genera automáticamente un TEXT
    private String description;

    // Un producto puede tener muchas imágenes
    // cascade = CascadeType.ALL: Operaciones en Producto se propagan a Image (ej. si borras un producto, borra sus imágenes)
    // orphanRemoval = true: Si una imagen se desvincula de un producto, se borra
    // fetch = FetchType.LAZY: Carga las imágenes solo cuando se accede a ellas
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category", // Nombre de la tabla de unión (o tabla intermedia)
            joinColumns = @JoinColumn(name = "product_id"), //Columna de la tabla de unión que referencia a Producto
            inverseJoinColumns = @JoinColumn(name = "category_id"), // Columna de la tabla de unión que referencia a Categoría
            indexes = {
                    @Index(name = "idx_pc_category", columnList = "category_id"),
                    @Index(name = "idx_pc_product", columnList = "product_id")
            }
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_feature", // Nombre de la tabla de unión (o tabla intermedia)
            joinColumns = @JoinColumn(name = "product_id"), //Columna de la tabla de unión que referencia a Producto
            inverseJoinColumns = @JoinColumn(name = "feature_id"), // Columna de la tabla de unión que referencia a la característica
            indexes = {
                    @Index(name = "idx_pf_feature", columnList = "feature_id"),
                    @Index(name = "idx_pf_product", columnList = "product_id")
            }
    )
    private List<Feature> features = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();

    private Double averageRating = 0.0;
    private Integer totalReviews = 0;
    private Integer passengerCapacity;
    private Integer baggageCapacity;
    private Integer numberOfDoors;
    private Double price;

    @Column(nullable = false)
    private Double baseDepositAmount; // El depósito heredado de la categoría ganadora

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserFavorite> favoritedBy = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_policy",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "policy_id"),
            indexes = {
                    @Index(name = "idx_pp_policy", columnList = "policy_id"),
                    @Index(name = "idx_pp_product", columnList = "product_id")
            }
    )
    private List<Policy> policies = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<ShareInteraction> shareInteractions = new ArrayList<>();

    @Version // Campo para el bloqueo optimista
    private Long version;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Make getMake() {
        return make;
    }

    public void setMake(Make make) {
        this.make = make;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductImage> getImages() {
        return Collections.unmodifiableList(this.images);
    }

    private void setImages(List<ProductImage> imagePaths) {
        this.images = imagePaths;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(this.categories);
    }

    private void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(this.features);
    }

    private void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(this.vehicles);
    }

    private void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Integer getBaggageCapacity() {
        return baggageCapacity;
    }

    public void setBaggageCapacity(Integer baggageCapacity) {
        this.baggageCapacity = baggageCapacity;
    }

    public Integer getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(Integer numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getBaseDepositAmount() {
        return baseDepositAmount;
    }

    public void setBaseDepositAmount(Double baseDepositAmount) {
        this.baseDepositAmount = baseDepositAmount;
    }

    public Set<UserFavorite> getFavoritedBy() {
        return this.favoritedBy;
    }

    private void setFavoritedBy(Set<UserFavorite> favoriteBy) {
        this.favoritedBy = favoriteBy;
    }

    public List<Policy> getPolicies() {
        return Collections.unmodifiableList(this.policies);
    }

    private void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public List<ShareInteraction> getShareInteractions() {
        return Collections.unmodifiableList(this.shareInteractions);
    }

    private void setShareInteractions(List<ShareInteraction> shareInteractions) {
        this.shareInteractions = shareInteractions;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        // 1. Comparación de referencia física
        if (this == o) return true;

        // 2. Verificación de clase (usamos instanceof para ser compatibles con Proxies)
        if (!(o instanceof Product that)) return false;

        // 3. Si el ID es nulo, los objetos no son iguales (a menos que sean la misma instancia)
        // Esto es vital para objetos nuevos que aún no se han guardado
        if (this.id == null || that.getId() == null) {
            return false;
        }

        // 4. Comparación lógica por Identificador Único
        return Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        // Retornamos un valor constante para cumplir el contrato de Hash
        // y evitar que el objeto se "pierda" en un Set tras persistirlo.
        return getClass().hashCode();
    }

    // --- MÉTODOS DE CONVENIENCIA ---

    // Imágenes (OneToMany)
    public void addImage(ProductImage image) {
        if (image != null && !this.images.contains(image)) {
            this.images.add(image);
            if (image.getProduct() != this) {
                image.setProduct(this); // Sincroniza el hijo
            }
        }
    }

    public void removeImage(ProductImage image) {
        if (image != null && this.images.contains(image)) {
            this.images.remove(image);
            if (image.getProduct() == this) {
                image.setProduct(null); // Desvincula el hijo
            }
        }
    }


    // Categorías (ManyToMany - Owner Side)
    public void addCategory(Category category) {
        if (category != null && !this.categories.contains(category)) {
            this.categories.add(category);
            // Sincronizar el otro lado
            if (!category.getProducts().contains(this)) {
                //category.getProducts().add(this);
                category.addProduct(this);
            }
        }
    }

    public void removeCategory(Category category) {
        if (category != null && this.categories.contains(category)) {
            this.categories.remove(category);
            // Sincronizar el otro lado
            //category.getProducts().remove(this);
            category.removeProduct(this);
        }
    }

    // Características (ManyToMany - Owner Side)
    public void addFeature(Feature feature) {
        if (feature != null && !this.features.contains(feature)) {
            this.features.add(feature);
            if (!feature.getProducts().contains(this)) {
                feature.addProduct(this);
            }
        }
    }

    public void removeFeature(Feature feature) {
        if (feature != null && this.features.contains(feature)) {
            this.features.remove(feature);
            feature.removeProduct(this);
        }
    }


    // Vehiculo (OneToMany)
    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null && !this.vehicles.contains(vehicle)) {
            this.vehicles.add(vehicle);
            if (vehicle.getProduct() != this) {
                vehicle.setProduct(this);
            }
        }
    }

    public void removeVehicle(Vehicle vehicle) {
        if (vehicle != null && this.vehicles.contains(vehicle)) {
            this.vehicles.remove(vehicle);
            // Sincronización: Romper la relación inversa
            if (vehicle.getProduct() == this) {
                vehicle.setProduct(null);
            }
        }
    }

    public void clearAllFavorites() {
        // Al limpiar el Set y tener orphanRemoval=true,
        // Hibernate borrará todas las entradas en la tabla de unión.
        if (this.favoritedBy != null) {
            this.favoritedBy.clear();
        }
    }

    // Politicas (ManyToMany - Owner Side)
    public void addPolicy(Policy policy) {
        if (policy != null && !this.policies.contains(policy)) {
            this.policies.add(policy);
            if (!policy.getProducts().contains(this)) {
                policy.addProduct(this);
            }
        }
    }

    public void removePolicy(Policy policy) {
        if (policy != null && this.policies.contains(policy)) {
            this.policies.remove(policy);
            policy.removeProduct(this);
        }
    }

    // Interacciones (OneToMany)
    public void addShareInteraction(ShareInteraction interaction) {
        if (interaction != null) {
            this.shareInteractions.add(interaction);
            interaction.setProduct(this); // Sincroniza el otro lado
        }
    }

    /* Método para desvincular UNA interacción (sin borrarla de la BD)
    public void unlinkShareInteraction(ShareInteraction interaction) {
        if (interaction != null) {
            // 1. Remover de la lista local para consistencia en memoria
            this.shareInteractions.remove(interaction);

            // 2. IMPORTANTE: No dejarlo huérfano, sino setear NULL explícitamente
            interaction.setProduct(null);
        }
    }

    // Método hook de JPA: Se ejecuta AUTOMÁTICAMENTE antes de borrar el Producto
    @PreRemove
    public void unlinkAllInteractionsBeforeDelete() {
        // Recorremos una copia de la lista para evitar ConcurrentModificationException
        for (ShareInteraction interaction : new ArrayList<>(shareInteractions)) {
            unlinkShareInteraction(interaction);
        }
    }
    */

    //Método para borrado Lógico de Product
    public boolean hasActiveVehicles() {
        // Regla 1: No vehículos activos
        // Si está vacía, devuelve false.
        // Si tiene elementos, devuelve true solo si alguno no está borrado.
        return this.vehicles.stream().anyMatch(v -> !v.isDeleted());
    }


}

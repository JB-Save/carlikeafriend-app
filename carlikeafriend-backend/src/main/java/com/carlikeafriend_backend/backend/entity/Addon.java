package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addon", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Addon extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargeType chargeType; // Por defecto diario

    @Column(nullable = false)
    private Integer maxQuantityPerReservation; // Tope por defecto

    @Column(name = "max_chargeable_days")
    private Integer maxChargeableDays;

    @Version
    private Long Version;

    public Addon() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public Integer getMaxQuantityPerReservation() {
        return maxQuantityPerReservation;
    }

    public void setMaxQuantityPerReservation(Integer maxQuantityPerReservation) {
        this.maxQuantityPerReservation = maxQuantityPerReservation;
    }

    public Integer getMaxChargeableDays() {
        return maxChargeableDays;
    }

    public void setMaxChargeableDays(Integer maxChargeableDays) {
        this.maxChargeableDays = maxChargeableDays;
    }

    public Long getVersion() {
        return Version;
    }

    public void setVersion(Long version) {
        Version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Addon that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
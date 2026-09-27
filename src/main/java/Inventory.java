package com.supply.chain.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "location_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(nullable = false)
    private Integer damagedQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryTransaction> transactions;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DamageReport> damageReports;

    @Transient
    public Integer getAvailableQuantity() {
        return quantity - damagedQuantity;
    }
}
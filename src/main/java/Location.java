package com.supply.chain.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location extends Auditable {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlertThreshold> alertThresholds;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DamageReport> damageReports;

    public boolean hasInventories() {
        return inventories != null && !inventories.isEmpty();
    }

    public boolean hasAlertThresholds() {
        return alertThresholds != null && !alertThresholds.isEmpty();
    }

    public boolean hasDamageReports() {
        return damageReports != null && !damageReports.isEmpty();
    }

}

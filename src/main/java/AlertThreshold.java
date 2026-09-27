package com.supply.chain.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_thresholds",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "location_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertThreshold extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false)
    private Integer lowStockThreshold;

    private Integer overstockThreshold;

    @Column(nullable = false)
    private Boolean emailAlertsEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_severity_id")
    private Status alertSeverity;
}

package com.supply.chain.inventory.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "global_inventory_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalInventorySetting extends Auditable {

    @Column(nullable = false)
    private String defaultSafetyStockCalculationMethod;

    @Column(nullable = false)
    private Integer defaultSafetyStockValue;

    @Column(nullable = false)
    private Integer defaultLeadTimeDays;

    @Column(nullable = false)
    private Integer defaultAlertThresholdPercentage;

}

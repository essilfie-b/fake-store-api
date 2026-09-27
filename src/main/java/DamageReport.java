package com.supply.chain.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "damage_reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageReport extends Auditable {
    @Column(unique = true, nullable = false)
    private String reportNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(nullable = false)
    private String reportedBy;

    private String inspectionOfficer;
    private LocalDate inspectionDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal estimatedLossValue;

    @ElementCollection
    @CollectionTable(name = "damage_report_photos", joinColumns = @JoinColumn(name = "damage_report_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;
}
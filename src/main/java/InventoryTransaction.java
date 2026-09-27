package com.supply.chain.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private Status transactionType;

    @Column(nullable = false)
    private Integer quantity;

    private String referenceNumber;

    private String receivedBy;

    private String supplier;
}

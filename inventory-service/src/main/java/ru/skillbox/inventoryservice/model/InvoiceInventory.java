package ru.skillbox.inventoryservice.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "invoice_inventories")
@Data
public class InvoiceInventory {

    @EmbeddedId
    private InvoiceInventoryKey invoiceInventoryKey;

    @ManyToOne
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @MapsId("inventoryId")
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    private Integer count;
}

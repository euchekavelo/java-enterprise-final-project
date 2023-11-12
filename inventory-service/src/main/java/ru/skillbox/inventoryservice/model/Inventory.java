package ru.skillbox.inventoryservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "inventory")
@Setter
@Getter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer count;
    private Integer costPerPiece;
    private Long userId;

    @OneToMany(mappedBy = "inventory")
    private Set<InvoiceInventory> invoiceInventorySet = new HashSet<>();
}

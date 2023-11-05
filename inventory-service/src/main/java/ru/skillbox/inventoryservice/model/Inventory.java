package ru.skillbox.inventoryservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer count;
    private Integer costPerPiece;
    private Long userId;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private Set<InvoiceInventory> invoiceInventorySet = new HashSet<>();
}

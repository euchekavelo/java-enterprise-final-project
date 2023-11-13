package ru.skillbox.inventoryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Integer count;

    @JsonIgnore
    private Integer costPerPiece;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    @OneToMany(mappedBy = "inventory")
    private Set<InvoiceInventory> invoiceInventorySet = new HashSet<>();
}

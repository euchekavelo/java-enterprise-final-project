package ru.skillbox.inventoryservice.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "invoice")
    Set<InvoiceInventory> invoiceInventorySet = new HashSet<>();
}

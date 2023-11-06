package ru.skillbox.deliveryservice.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.skillbox.deliveryservice.model.enums.DeliveryStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceId;

    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @CreationTimestamp
    private LocalDateTime creationDate;
}

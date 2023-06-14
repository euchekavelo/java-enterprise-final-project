package ru.skillbox.paymentservice.model;

import javax.persistence.*;

@Entity
@Table(name = "payment")
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Double balance;
}

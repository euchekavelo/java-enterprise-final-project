package ru.skillbox.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.skillbox.paymentservice.model.enums.TransactionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sum;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @CreationTimestamp
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @UpdateTimestamp
    private LocalDateTime modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}

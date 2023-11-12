package ru.skillbox.orderservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public ProductDetail(Long id, Long productId, Integer count, Order order) {
        this.id = id;
        this.productId = productId;
        this.count = count;
        this.order = order;
    }
}

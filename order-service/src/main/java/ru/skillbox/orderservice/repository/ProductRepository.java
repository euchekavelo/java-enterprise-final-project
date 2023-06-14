package ru.skillbox.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.orderservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
